package com.amalitech.bloggingplatform.dao.impl;

import com.amalitech.bloggingplatform.dao.UserDAO;
import com.amalitech.bloggingplatform.model.User;

import java.util.List;
import java.util.Optional;

public class UserDAOImpl implements UserDAO {
    @Override
    public User save(User entity) {
        return null;
    }

    @Override
    public Optional<User> findById(String id) {
        return Optional.empty();
    }

    @Override
    public List<User> findAll() {
        return List.of();
    }

    @Override
    public boolean update(User entity) {
        return false;
    }

    @Override
    public boolean deleteById(String id) {
        return false;
    }
}
