package com.amalitech.bloggingplatform.service;

import com.amalitech.bloggingplatform.model.User;

import java.util.List;

public interface UserService {

    User create(User user);

    User login(String email, String password);

    User update(User user);

    boolean delete(String userId);

    User getById(String userId);

    User getByEmail(String email);

    List<User> getAll();
}

