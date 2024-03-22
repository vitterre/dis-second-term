package com.technokratos.agona.core;

import com.technokratos.agona.annotation.Column;
import com.technokratos.agona.annotation.Entity;
import com.technokratos.agona.annotation.ManyToOne;
import com.technokratos.agona.annotation.PrimaryKey;
import com.technokratos.agona.orm.EntityManager;
import com.technokratos.agona.util.EntityClassLoader;
import lombok.val;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

@Component
public class EntityManagerImpl implements EntityManager {

    private static final String GET_TABLES = """
            SELECT
                table_name
            FROM
                information_schema.tables
            WHERE
                table_type = 'BASE TABLE'
            AND
                table_schema NOT IN ('pg_catalog', 'information_schema');
            """;

    private static final String GET_TABLE_FIELDS = """
            SELECT a.attname
            FROM pg_catalog.pg_attribute a
            WHERE a.attrelid = (
            	SELECT c.oid FROM pg_catalog.pg_class c\s
            	LEFT JOIN pg_catalog.pg_namespace n ON n.oid = c.relnamespace
            	WHERE pg_catalog.pg_table_is_visible(c.oid) AND c.relname = ?
            )
            AND a.attnum > 0 AND NOT a.attisdropped
            """;

    private final DataSource dataSource;

    private Map<String, Object> entities;

    public EntityManagerImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void validateEntities() {
        val entityClasses =
                EntityClassLoader.findAnnotatedClassesInPackage("com.technokratos.agona.model", Entity.class);

        val entitySimpleClassTree = new HashMap<String, List<String>>();
        for (val clazz : entityClasses) {
            val entity = clazz.getAnnotation(Entity.class);
            entitySimpleClassTree.put(entity.tableName(), EntityClassLoader.getFieldNames(clazz));
        }
        System.out.println(entitySimpleClassTree);


        val databaseTableTree = new HashMap<String, List<String>>();
        val tableNames = getTableNames();
        for (val tableName : tableNames) {
            val fieldNames = getTableFieldNames(tableName);
            databaseTableTree.put(tableName, fieldNames);
        }
        System.out.println(databaseTableTree);

        if (!entitySimpleClassTree.equals(databaseTableTree)) {
            throw new RuntimeException("Wrong entity model present!");
        }
    }

    private List<String> getTableNames() {
        try (val connection = dataSource.getConnection();
             val statement = connection.prepareStatement(GET_TABLES);
             val rs = statement.executeQuery()) {

            val tableNames = new ArrayList<String>();

            while (rs.next()) {
                val tableName = rs.getString("table_name");
                if (tableName.equals("databasechangelog")
                        || tableName.equals("databasechangeloglock")
                        || tableName.equals("rs_schema_version")
                ) {
                    continue;
                }
                tableNames.add(tableName);
            }
            return tableNames;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private List<String> getTableFieldNames(String tableName) {
        try (val connection = dataSource.getConnection();
             val statement = connection.prepareStatement(GET_TABLE_FIELDS)) {
            statement.setString(1, tableName);

            val rs = statement.executeQuery();

            val tableFieldNames = new ArrayList<String>();

            while (rs.next()) {
                val fieldName = rs.getString("attname");
                tableFieldNames.add(fieldName);
            }

            rs.close();

            return tableFieldNames;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public <T> T save(T entity) {
        val tableName = entity.getClass().getAnnotation(Entity.class).tableName();
        val sqlStringBuilder = new StringBuilder("insert into %s(".formatted(tableName));

        val declaredFields = entity.getClass().getDeclaredFields();

        for (int i = 0; i < declaredFields.length; i++) {
            val field = declaredFields[i];
            val fieldColumnName = field.getAnnotation(Column.class).name();
            sqlStringBuilder.append(fieldColumnName);

            if (i != declaredFields.length - 1) {
                sqlStringBuilder.append(", ");
            } else {
                sqlStringBuilder.append(")");
            }
        }

        sqlStringBuilder.append(" values (");

        for (int i = 0; i < declaredFields.length; i++) {
            if (i != declaredFields.length - 1) {
                sqlStringBuilder.append("?, ");
            } else {
                sqlStringBuilder.append("?)");
            }
        }

        val SQL = sqlStringBuilder.toString();
        System.out.println(SQL);

        try (val connection = dataSource.getConnection();
             val statement = connection.prepareStatement(SQL)) {

            for (int i = 0; i < declaredFields.length; i++) {
                val field = declaredFields[i];
                field.setAccessible(true);
                statement.setObject(i + 1, field.get(entity));
            }

            statement.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return entity;
    }

    @Override
    public <T> void remove(Class<T> entityClass, Object key) {
        val tableName = entityClass.getAnnotation(Entity.class).tableName();
        var primaryKeyFieldName = "";

        for (val field : entityClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(PrimaryKey.class)) {
                primaryKeyFieldName = field.getAnnotation(Column.class).name();
            }
        }

        val SQL = "delete from %s where %s = ?".formatted(tableName, primaryKeyFieldName);

        try (val connection = dataSource.getConnection();
             val statement = connection.prepareStatement(SQL)) {
            statement.setObject(1, key);

            statement.execute();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> Optional<T> findById(Class<T> entityClass, Object key) {
        val tableName = entityClass.getAnnotation(Entity.class).tableName();
        var primaryKeyFieldName = "";

        for (val field : entityClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(PrimaryKey.class)) {
                primaryKeyFieldName = field.getAnnotation(Column.class).name();
            }
        }

        val SQL = "select * from %s where %s = ?".formatted(tableName, primaryKeyFieldName);

        try (val connection = dataSource.getConnection();
             val statement = connection.prepareStatement(SQL)) {
            statement.setObject(1, key);

            System.out.println(SQL);

            val rs = statement.executeQuery();

            val entities = new ArrayList<T>();

            while (rs.next()) {
                val constructor = entityClass.getDeclaredConstructor();
                constructor.setAccessible(true);
                T entity = constructor.newInstance();

                for (val field : entityClass.getDeclaredFields()) {
                    field.setAccessible(true);
                    val value = rs.getObject(field.getAnnotation(Column.class).name());

                    if (field.isAnnotationPresent(ManyToOne.class)) {
                        val innerEntity = findById(field.getType(), value);
                        field.set(entity, innerEntity.orElseThrow());
                        continue;
                    }
                    field.set(entity, value);
                }

                entities.add(entity);
            }

            if (entities.isEmpty()) {
                return Optional.empty();
            }

            return Optional.of(entities.getFirst());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> List<T> findAll(Class<T> entityClass) {
        val SQL = "select * from %s";
        val tableName = entityClass.getAnnotation(Entity.class).tableName();

        try (val connection = dataSource.getConnection();
             val statement = connection.prepareStatement(SQL.formatted(tableName))) {

            val rs = statement.executeQuery();

            val entities = new ArrayList<T>();

            while (rs.next()) {
                val constructor = entityClass.getDeclaredConstructor();
                constructor.setAccessible(true);
                T entity = constructor.newInstance();

                for (val field : entityClass.getDeclaredFields()) {
                    field.setAccessible(true);
                    val value = rs.getObject(field.getAnnotation(Column.class).name());
                    field.set(entity, value);
                }

                entities.add(entity);
            }

            return entities;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
