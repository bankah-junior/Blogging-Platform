package com.amalitech.bloggingplatform.dao;

import com.amalitech.bloggingplatform.model.User;

import java.util.Optional;

public interface UserDAO extends BaseDAO<User> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> login(String email, String password);

    boolean updateUserDetails(User user);

    boolean updatePassword(String userId, String newPasswordHash);

}

