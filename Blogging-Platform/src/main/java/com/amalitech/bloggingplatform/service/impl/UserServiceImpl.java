package com.amalitech.bloggingplatform.service.impl;

import com.amalitech.bloggingplatform.cache.Cache;
import com.amalitech.bloggingplatform.cache.LruCache;
import com.amalitech.bloggingplatform.model.dto.request.LoginRequest;
import com.amalitech.bloggingplatform.model.dto.request.RegisterRequest;
import com.amalitech.bloggingplatform.model.dto.request.UpdatePasswordRequest;
import com.amalitech.bloggingplatform.model.dto.request.UpdateUserDetailRequest;
import com.amalitech.bloggingplatform.model.dto.response.PageResponse;
import com.amalitech.bloggingplatform.model.dto.response.UserResponse;
import com.amalitech.bloggingplatform.model.entity.User;
import com.amalitech.bloggingplatform.service.UserService;
import com.amalitech.bloggingplatform.utils.PasswordUtils;
import com.amalitech.bloggingplatform.utils.apis.ApiClient;
import com.amalitech.bloggingplatform.utils.apis.ApiResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class UserServiceImpl implements UserService {

    private final Cache<String, User> userCache;
    private final Gson gson;

    public UserServiceImpl(Cache<String, User> userCache) {
        this.userCache = userCache;
        this.gson = new Gson();
    }

    public UserServiceImpl() {
        this(new LruCache<>(50));
    }

    @Override
    public User create(User user) {
        if (user == null) {
            return null;
        }
        RegisterRequest request = new RegisterRequest(
                user.getEmail(),
                user.getUsername(),
                user.getPasswordHash()
        );
        ApiResponse response = ApiClient.registerUser(request);
        User created = parseSingleUserFromResponse(response);
        if (created != null) {
            userCache.put(created.getId(), created);
        }
        return created;
    }

    @Override
    public User login(String email, String password) {
        LoginRequest request = new LoginRequest(email, password);
        ApiResponse response = ApiClient.loginUser(request);
        return parseSingleUserFromResponse(response);
    }

    @Override
    public User update(User user) {
        if (user == null || user.getId() == null) {
            return null;
        }
        UpdateUserDetailRequest request = new UpdateUserDetailRequest();
        request.setUsername(user.getUsername());
        request.setEmail(user.getEmail());

        ApiResponse response = ApiClient.updateUser(user.getId(), request);
        User updated = parseSingleUserFromResponse(response);
        if (updated != null) {
            userCache.put(updated.getId(), updated);
        }
        return updated;
    }

    @Override
    public boolean delete(String userId) {
        if (userId == null) {
            return false;
        }
        ApiResponse response = ApiClient.deleteUser(userId);
        boolean success = isSuccess(response);
        if (success) {
            userCache.remove(userId);
        }
        return success;
    }

    @Override
    public User getById(String userId) {
        if (userId == null) {
            return null;
        }
        ApiResponse response = ApiClient.getUserById(userId);
        User user = parseSingleUserFromResponse(response);
        if (user != null) {
            userCache.put(user.getId(), user);
        }
        return user;
    }

    @Override
    public User getByEmail(String email) {
        if (email == null || email.isBlank()) {
            return null;
        }
        ApiResponse response = ApiClient.getUserByEmail(email);
        return parseSingleUserFromResponse(response);
    }

    @Override
    public List<User> getAll() {
        ApiResponse response = ApiClient.getAllUsers(0, 100);
        PageResponse<UserResponse> page = parseUserPageFromResponse(response);
        if (page == null || page.getContent() == null) {
            return Collections.emptyList();
        }
        List<User> users = toUserEntities(page.getContent());
        users.forEach(u -> {
            if (u.getId() != null) {
                userCache.put(u.getId(), u);
            }
        });
        return users;
    }

    @Override
    public boolean updateUserDetails(User user) {
        if (user == null || user.getId() == null) {
            return false;
        }
        UpdateUserDetailRequest request = new UpdateUserDetailRequest();
        request.setUsername(user.getUsername());
        request.setEmail(user.getEmail());

        ApiResponse response = ApiClient.updateUserDetails(user.getId(), request);
        boolean success = isSuccess(response);
        if (success) {
            userCache.put(user.getId(), user);
        }
        return success;
    }

    @Override
    public boolean changePassword(String userId, String oldPassword, String newPassword) {
        if (userId == null || oldPassword == null || newPassword == null) {
            return false;
        }
        // Validate locally using the cached user if available
        User cached = userCache.get(userId);
        if (cached != null && !PasswordUtils.verifyPassword(oldPassword, cached.getPasswordHash())) {
            return false;
        }

        UpdatePasswordRequest request = new UpdatePasswordRequest();
        request.setPassword(newPassword);

        ApiResponse response = ApiClient.changeUserPasswordById(userId, request);
        boolean success = isSuccess(response);
        if (success) {
            userCache.remove(userId);
        }
        return success;
    }

    @Override
    public User getByUsername(String username) {
        // There is no dedicated endpoint yet; approximate by scanning all users.
        if (username == null || username.isBlank()) {
            return null;
        }
        return getAll().stream()
                .filter(u -> username.equals(u.getUsername()))
                .findFirst()
                .orElse(null);
    }

    // ---------- Helpers ----------

    private boolean isSuccess(ApiResponse response) {
        return response != null && "success".equalsIgnoreCase(response.getStatus());
    }

    private User parseSingleUserFromResponse(ApiResponse response) {
        if (!isSuccess(response) || response.getData() == null) {
            return null;
        }
        UserResponse dto = gson.fromJson(response.getData(), UserResponse.class);
        if (dto == null) {
            return null;
        }
        User user = new User();
        user.setId(dto.getId());
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        // Password hash is not exposed by the API; leave as-is/null.
        user.setCreatedAt(dto.getCreatedAt());
        user.setUpdatedAt(dto.getUpdatedAt());
        return user;
    }

    private PageResponse<UserResponse> parseUserPageFromResponse(ApiResponse response) {
        if (!isSuccess(response) || response.getData() == null) {
            return null;
        }
        Type pageType = new TypeToken<PageResponse<UserResponse>>() {}.getType();
        return gson.fromJson(response.getData(), pageType);
    }

    private List<User> toUserEntities(List<UserResponse> dtos) {
        if (dtos == null || dtos.isEmpty()) {
            return Collections.emptyList();
        }
        return dtos.stream().map(dto -> {
            User u = new User();
            u.setId(dto.getId());
            u.setUsername(dto.getUsername());
            u.setEmail(dto.getEmail());
            u.setCreatedAt(dto.getCreatedAt());
            u.setUpdatedAt(dto.getUpdatedAt());
            return u;
        }).toList();
    }
}

