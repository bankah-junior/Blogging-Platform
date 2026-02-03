package com.amalitech.SpringBootBloggingApp.repository;

import java.util.List;
import java.util.Optional;

public interface BaseRepository<T> {

    T save(T entity);

    Optional<T> findById(String id);

    List<T> findAll();

    List<T> findAll(int skip, int limit);

    long count();

    boolean update(T entity);

    boolean deleteById(String id);
}
