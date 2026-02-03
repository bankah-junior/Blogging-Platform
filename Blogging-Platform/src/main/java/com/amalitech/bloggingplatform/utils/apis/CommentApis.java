package com.amalitech.bloggingplatform.utils.apis;

import com.amalitech.bloggingplatform.model.dto.request.*;
import com.amalitech.bloggingplatform.utils.ValidationUtils;
import com.amalitech.bloggingplatform.utils.exceptions.UserInputsException;
import com.google.gson.Gson;

public class CommentApis {
    private static final String baseUrl = "http://localhost:8080/api/v1/comments";
    private static final Gson gson = new Gson();

    private CommentApis() {
        // Utility class - prevent instantiation
    }

    // Create a comment
    public static ApiResponse createComment(CreateCommentRequest request) {
        if (request == null) {
            throw new UserInputsException("Create comment request cannot be null");
        }
        String[] endpoint = new String[]{baseUrl + "/create", "POST"};
        String requestBody = gson.toJson(request);
        return FetchFromApi.fetchFromApi(endpoint[0], endpoint[1], requestBody);
    }

    // Update a comment
    public static ApiResponse updateCommentById(String commentId, UpdateCommentRequest request) {
        ValidationUtils.validateAndThrow(ValidationUtils.isValidObjectId(commentId), "Invalid comment ID format");
        if (request == null) {
            throw new UserInputsException("Update comment request cannot be null");
        }
        String[] endpoint = new String[]{baseUrl + "/" + commentId, "PUT"};
        String requestBody = gson.toJson(request);
        return FetchFromApi.fetchFromApi(endpoint[0], endpoint[1], requestBody);
    }

    // Update a comment
    public static ApiResponse updateComment(UpdateCommentRequest request) {
        if (request == null) {
            throw new UserInputsException("Update comment request cannot be null");
        }
        String[] endpoint = new String[]{baseUrl + "/update", "PUT"};
        String requestBody = gson.toJson(request);
        return FetchFromApi.fetchFromApi(endpoint[0], endpoint[1], requestBody);
    }

    // Get comment by id
    public static ApiResponse getCommentById(String commentId) {
        ValidationUtils.validateAndThrow(ValidationUtils.isValidObjectId(commentId), "Invalid comment ID format");
        String[] endpoint = new String[]{baseUrl + "/" + commentId, "GET"};
        return FetchFromApi.fetchFromApi(endpoint[0], endpoint[1], null);
    }

    // Get comment by user id
    public static ApiResponse getCommentByUserId(String userId) {
        ValidationUtils.validateAndThrow(ValidationUtils.isValidObjectId(userId), "Invalid user ID format");
        String[] endpoint = new String[]{baseUrl + "/user/" + userId, "GET"};
        return FetchFromApi.fetchFromApi(endpoint[0], endpoint[1], null);
    }

    // Get comment by post id
    public static ApiResponse getCommentByPostId(String postId) {
        ValidationUtils.validateAndThrow(ValidationUtils.isValidObjectId(postId), "Invalid post ID format");
        String[] endpoint = new String[]{baseUrl + "/post/" + postId, "GET"};
        return FetchFromApi.fetchFromApi(endpoint[0], endpoint[1], null);
    }

    // Get all comments
    public static ApiResponse getAllComments() {
        String[] endpoint = new String[]{baseUrl + "/all", "GET"};
        return FetchFromApi.fetchFromApi(endpoint[0], endpoint[1], null);
    }

    // Delete a comment by id
    public static ApiResponse deleteCommentById(String commentId) {
        ValidationUtils.validateAndThrow(ValidationUtils.isValidObjectId(commentId), "Invalid comment ID format");
        String[] endpoint = new String[]{baseUrl + "/delete/" + commentId, "DELETE"};
        return FetchFromApi.fetchFromApi(endpoint[0], endpoint[1], null);
    }

    // Get comments by page and size
    public static ApiResponse getCommentsByPage(int page, int size) {
        ValidationUtils.validateAndThrow(page >= 0, "Page number must be 0 or greater");
        ValidationUtils.validateAndThrow(size > 0 && size <= 100, "Page size must be between 1 and 100");
        String[] endpoint = new String[]{baseUrl + "/page?page=" + page + "&size=" + size, "GET"};
        return FetchFromApi.fetchFromApi(endpoint[0], endpoint[1], null);
    }

    // Get comments by post id with pagination
    public static ApiResponse getCommentsByPostIdPaginated(String postId, int page, int size) {
        ValidationUtils.validateAndThrow(ValidationUtils.isValidObjectId(postId), "Invalid post ID format");
        ValidationUtils.validateAndThrow(page >= 0, "Page number must be 0 or greater");
        ValidationUtils.validateAndThrow(size > 0 && size <= 100, "Page size must be between 1 and 100");
        String[] endpoint = new String[]{baseUrl + "/post/" + postId + "?page=" + page + "&size=" + size, "GET"};
        return FetchFromApi.fetchFromApi(endpoint[0], endpoint[1], null);
    }

    // Get comments by user id with pagination
    public static ApiResponse getCommentsByUserIdPaginated(String userId, int page, int size) {
        ValidationUtils.validateAndThrow(ValidationUtils.isValidObjectId(userId), "Invalid user ID format");
        ValidationUtils.validateAndThrow(page >= 0, "Page number must be 0 or greater");
        ValidationUtils.validateAndThrow(size > 0 && size <= 100, "Page size must be between 1 and 100");
        String[] endpoint = new String[]{baseUrl + "/user/" + userId + "?page=" + page + "&size=" + size, "GET"};
        return FetchFromApi.fetchFromApi(endpoint[0], endpoint[1], null);
    }

    // Search comments by content
    public static ApiResponse searchCommentsByContent(String content) {
        ValidationUtils.validateAndThrow(ValidationUtils.isNotBlank(content), "Search content cannot be empty");
        ValidationUtils.validateAndThrow(content.length() <= 500, "Search content must be 500 characters or less");
        String[] endpoint = new String[]{baseUrl + "/search?content=" + content, "GET"};
        return FetchFromApi.fetchFromApi(endpoint[0], endpoint[1], null);
    }
}
