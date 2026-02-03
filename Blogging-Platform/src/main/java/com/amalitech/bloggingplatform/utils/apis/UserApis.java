package com.amalitech.bloggingplatform.utils.apis;

import com.amalitech.bloggingplatform.model.dto.request.*;
import com.amalitech.bloggingplatform.utils.ValidationUtils;
import com.amalitech.bloggingplatform.utils.exceptions.UserInputsException;
import com.google.gson.Gson;

public class UserApis {
    private static final String baseUrl = "http://localhost:8080/api/v1/users";
    private static final Gson gson = new Gson();

    private UserApis() {
        // Utility class - prevent instantiation
    }

    // Get user by id
    public static ApiResponse getUserById(String id) {
        ValidationUtils.validateAndThrow(ValidationUtils.isValidObjectId(id), "Invalid user ID format");
        String[] endpoint = new String[]{baseUrl + "/" + id, "GET"};
        return FetchFromApi.fetchFromApi(endpoint[0], endpoint[1], null);
    }

    // Update user by id
    public static ApiResponse updateUserById(String id, UpdateUserDetailRequest request) {
        ValidationUtils.validateAndThrow(ValidationUtils.isValidObjectId(id), "Invalid user ID format");
        if (request == null) {
            throw new UserInputsException("Update user request cannot be null");
        }
        String[] endpoint = new String[]{baseUrl + "/" + id, "PUT"};
        String requestBody = gson.toJson(request);
        return FetchFromApi.fetchFromApi(endpoint[0], endpoint[1], requestBody);
    }

    // Delete user by id
    public static ApiResponse deleteUserById(String id) {
        ValidationUtils.validateAndThrow(ValidationUtils.isValidObjectId(id), "Invalid user ID format");
        String[] endpoint = new String[]{baseUrl + "/" + id, "DELETE"};
        return FetchFromApi.fetchFromApi(endpoint[0], endpoint[1], null);
    }

    // Change user password by id
    public static ApiResponse changeUserPasswordById(String id, UpdatePasswordRequest request) {
        ValidationUtils.validateAndThrow(ValidationUtils.isValidObjectId(id), "Invalid user ID format");
        if (request == null) {
            throw new UserInputsException("Password change request cannot be null");
        }
        String[] endpoint = new String[]{baseUrl + "/" + id + "/change-password", "PUT"};
        String requestBody = gson.toJson(request);
        return FetchFromApi.fetchFromApi(endpoint[0], endpoint[1], requestBody);
    }

    // Register user
    public static ApiResponse registerUser(RegisterRequest request) {
        if (request == null) {
            throw new UserInputsException("Register request cannot be null");
        }
        String[] endpoint = new String[]{baseUrl + "/register", "POST"};
        String requestBody = gson.toJson(request);
        return FetchFromApi.fetchFromApi(endpoint[0], endpoint[1], requestBody);
    }

    // Login user
    public static ApiResponse loginUser(LoginRequest request) {
        if (request == null) {
            throw new UserInputsException("Login request cannot be null");
        }
        String[] endpoint = new String[]{baseUrl + "/login", "POST"};
        String requestBody = gson.toJson(request);
        return FetchFromApi.fetchFromApi(endpoint[0], endpoint[1], requestBody);
    }

    // Create user
    public static ApiResponse createUser(CreateUserRequest request) {
        if (request == null) {
            throw new UserInputsException("Create user request cannot be null");
        }
        String[] endpoint = new String[]{baseUrl, "POST"};
        String requestBody = gson.toJson(request);
        return FetchFromApi.fetchFromApi(endpoint[0], endpoint[1], requestBody);
    }

    // Get all users
    public static ApiResponse getAllUsers(int page, int size) {
        ValidationUtils.validateAndThrow(page >= 0, "Page number must be 0 or greater");
        ValidationUtils.validateAndThrow(size > 0 && size <= 100, "Page size must be between 1 and 100");
        String[] endpoint = new String[]{baseUrl + "?page=" + page + "&size=" + size, "GET"};
        return FetchFromApi.fetchFromApi(endpoint[0], endpoint[1], null);
    }

    // Get user by email
    public static ApiResponse getUserByEmail(String email) {
        ValidationUtils.validateAndThrow(ValidationUtils.isEmailValid(email), "Invalid email format");
        String[] endpoint = new String[]{baseUrl + "/email/" + email, "GET"};
        return FetchFromApi.fetchFromApi(endpoint[0], endpoint[1], null);
    }

    // Update user details
    public static ApiResponse updateUserDetails(String id, UpdateUserDetailRequest request) {
        ValidationUtils.validateAndThrow(ValidationUtils.isValidObjectId(id), "Invalid user ID format");
        if (request == null) {
            throw new UserInputsException("Update user detail request cannot be null");
        }
        String[] endpoint = new String[]{baseUrl + "/" + id + "/details", "PUT"};
        String requestBody = gson.toJson(request);
        return FetchFromApi.fetchFromApi(endpoint[0], endpoint[1], requestBody);
    }

    // Update user password
    public static ApiResponse updateUserPassword(String id, UpdatePasswordRequest request) {
        ValidationUtils.validateAndThrow(ValidationUtils.isValidObjectId(id), "Invalid user ID format");
        if (request == null) {
            throw new UserInputsException("Update password request cannot be null");
        }
        String[] endpoint = new String[]{baseUrl + "/" + id + "/password", "PUT"};
        String requestBody = gson.toJson(request);
        return FetchFromApi.fetchFromApi(endpoint[0], endpoint[1], requestBody);
    }
}
