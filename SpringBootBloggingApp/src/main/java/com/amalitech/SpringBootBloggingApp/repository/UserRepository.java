package com.amalitech.SpringBootBloggingApp.repository;

import com.amalitech.SpringBootBloggingApp.model.entity.User;

import java.util.Optional;

public interface UserRepository extends BaseRepository<User> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> login(String email, String password);

    boolean updateUserDetails(String userId, User user);

    boolean updatePassword(String userId, String newPasswordHash);
}
