package com.technokratos.agona.orm;

import java.util.List;
import java.util.Optional;

public interface EntityManager {
    void validateEntities();

    <T> T save(T entity);
    <T> void remove(Class<T> entityClass, Object key);
    <T> Optional<T> findById(Class<T> entityClass, Object key);
    <T> List<T> findAll(Class<T> entityClass);
}
