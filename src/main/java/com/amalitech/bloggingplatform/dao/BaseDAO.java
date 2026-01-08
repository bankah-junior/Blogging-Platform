package com.amalitech.bloggingplatform.dao;

import java.util.List;
import java.util.Optional;

public interface BaseDAO<T> {

    T save(T entity);

    Optional<T> findById(String id);

    List<T> findAll();

    boolean update(T entity);

    boolean deleteById(String id);
}
