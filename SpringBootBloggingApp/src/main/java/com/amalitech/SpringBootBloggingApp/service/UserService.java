package com.amalitech.SpringBootBloggingApp.service;

import com.amalitech.SpringBootBloggingApp.model.dto.request.LoginRequest;
import com.amalitech.SpringBootBloggingApp.model.dto.request.RegisterRequest;
import com.amalitech.SpringBootBloggingApp.model.dto.request.UpdateUserDetailRequest;
import com.amalitech.SpringBootBloggingApp.model.dto.request.UpdateUserRequest;
import com.amalitech.SpringBootBloggingApp.model.dto.response.PageResponse;
import com.amalitech.SpringBootBloggingApp.model.dto.response.UserResponse;
import com.amalitech.SpringBootBloggingApp.model.entity.User;

import java.util.List;

public interface UserService {

    UserResponse create(RegisterRequest user);

    UserResponse login(LoginRequest user);

    UserResponse update(UpdateUserRequest user);

    boolean delete(String userId);

    UserResponse getById(String userId);

    UserResponse getByEmail(String email);

    List<UserResponse> getAll();

    PageResponse<User> getAllPaginated(int page, int size);

    boolean updateUserDetails(String userId, UpdateUserDetailRequest user);

    boolean changePassword(String userId, String oldPassword, String newPassword);

    UserResponse getByUsername(String username);
}
