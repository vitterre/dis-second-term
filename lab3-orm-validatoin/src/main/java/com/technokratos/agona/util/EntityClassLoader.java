package com.technokratos.agona.util;

import com.technokratos.agona.annotation.Column;
import com.technokratos.agona.annotation.ManyToOne;
import lombok.val;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

public class EntityClassLoader {
    public static List<Class<?>> findAnnotatedClassesInPackage(String packageName, Class<? extends Annotation> annotationClass) {
        val annotatedClasses = new ArrayList<Class<?>>();

        try {
            val classLoader = Thread.currentThread().getContextClassLoader();
            val path = packageName.replace('.', '/');
            val resource = classLoader.getResource(path);
            if (resource == null) {
                throw new IOException("Package not found: " + packageName);
            }

            val directory = new File(resource.getFile());
            if (directory.exists()) {
                val files = directory.listFiles();
                if (files != null) {
                    for (File file : files) {
                        if (file.getName().endsWith(".class")) {
                            val className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
                            val clazz = Class.forName(className);
                            if (clazz.isAnnotationPresent(annotationClass)) {
                                annotatedClasses.add(clazz);
                            }
                        }
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return annotatedClasses;
    }

    public static List<String> getFieldNames(Class<?> clazz) {
        val fieldNames = new ArrayList<String>();

        val fields = clazz.getDeclaredFields();

        for (val field : fields) {
            val tableField = field.getAnnotation(Column.class);
            fieldNames.add(tableField.name());
        }

        return fieldNames;
    }

    public static String convertCamelCaseToSnakeCase(String camelCaseString) {
        StringBuilder snakeCase = new StringBuilder();

        for (int i = 0; i < camelCaseString.length(); i++) {
            char c = camelCaseString.charAt(i);

            if (Character.isUpperCase(c)) {
                if (i > 0) {
                    snakeCase.append("_");
                }
                snakeCase.append(Character.toLowerCase(c));
            } else {
                snakeCase.append(c);
            }
        }

        return snakeCase.toString();
    }
}

