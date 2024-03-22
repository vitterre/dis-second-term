package com.technokratos.agona.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.val;
import org.flywaydb.core.Flyway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import javax.sql.DataSource;

@Configuration
@ComponentScan("com.technokratos.agona")
public class DatabaseConfig {
    @Bean
    public DataSource getDataSource() {
        System.out.println("getDataSource");
        val config = new HikariConfig();
        val url = "jdbc:postgresql://localhost:5432/music";
        config.setJdbcUrl(url);
        config.setUsername("agona_client");
        config.setPassword("0f2uzfhe0ux");
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        return new HikariDataSource(config);
    }

    @Bean(initMethod = "migrate")
    public Flyway flyway(DataSource dataSource) {
        Flyway flyway = new Flyway();
        flyway.setBaselineOnMigrate(true);
        flyway.setDataSource(dataSource);
        flyway.setTable("rs_schema_version");
        flyway.setSchemas("public");
        flyway.setLocations("filesystem:db/migration", "classpath:db/migration");
        return flyway;
    }
}
