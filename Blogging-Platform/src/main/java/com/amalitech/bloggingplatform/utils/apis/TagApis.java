package com.amalitech.bloggingplatform.utils.apis;

import com.amalitech.bloggingplatform.model.dto.request.*;
import com.amalitech.bloggingplatform.utils.ValidationUtils;
import com.amalitech.bloggingplatform.utils.exceptions.UserInputsException;
import com.google.gson.Gson;

public class TagApis {
    private static final String baseUrl = "http://localhost:8080/api/v1/tags";
    private static final Gson gson = new Gson();

    private TagApis() {
        // Utility class - prevent instantiation
    }

    // Create a tag
    public static ApiResponse createTag(CreateTagRequest request) {
        if (request == null) {
            throw new UserInputsException("Create tag request cannot be null");
        }
        String[] endpoint = new String[]{baseUrl + "/create", "POST"};
        String requestBody = gson.toJson(request);
        return FetchFromApi.fetchFromApi(endpoint[0], endpoint[1], requestBody);
    }

    // Get tag by id
    public static ApiResponse getTagById(String tagId) {
        ValidationUtils.validateAndThrow(ValidationUtils.isValidObjectId(tagId), "Invalid tag ID format");
        String[] endpoint = new String[]{baseUrl + "/" + tagId, "GET"};
        return FetchFromApi.fetchFromApi(endpoint[0], endpoint[1], null);
    }

    // Get tag by name
    public static ApiResponse getTagByName(String tagName) {
        ValidationUtils.validateAndThrow(ValidationUtils.isValidTagName(tagName), "Invalid tag name format");
        String[] endpoint = new String[]{baseUrl + "/name/" + tagName, "GET"};
        return FetchFromApi.fetchFromApi(endpoint[0], endpoint[1], null);
    }

    // Get all tags
    public static ApiResponse getAllTags() {
        String[] endpoint = new String[]{baseUrl + "/all", "GET"};
        return FetchFromApi.fetchFromApi(endpoint[0], endpoint[1], null);
    }

    // Update tag by id
    public static ApiResponse updateTag(String tagId, CreateTagRequest request) {
        ValidationUtils.validateAndThrow(ValidationUtils.isValidObjectId(tagId), "Invalid tag ID format");
        if (request == null) {
            throw new UserInputsException("Update tag request cannot be null");
        }
        String[] endpoint = new String[]{baseUrl + "/" + tagId, "PUT"};
        String requestBody = gson.toJson(request);
        return FetchFromApi.fetchFromApi(endpoint[0], endpoint[1], requestBody);
    }

    // Delete tag by id
    public static ApiResponse deleteTagById(String tagId) {
        ValidationUtils.validateAndThrow(ValidationUtils.isValidObjectId(tagId), "Invalid tag ID format");
        String[] endpoint = new String[]{baseUrl + "/" + tagId, "DELETE"};
        return FetchFromApi.fetchFromApi(endpoint[0], endpoint[1], null);
    }

    // Assign a tag to a post
    public static ApiResponse assignTagToPost(String postId, String tagId) {
        ValidationUtils.validateAndThrow(ValidationUtils.isValidObjectId(postId), "Invalid post ID format");
        ValidationUtils.validateAndThrow(ValidationUtils.isValidObjectId(tagId), "Invalid tag ID format");
        String[] endpoint = new String[]{baseUrl + "/" + postId + "/assign/" + tagId, "POST"};
        return FetchFromApi.fetchFromApi(endpoint[0], endpoint[1], null);
    }

    // Unassign a tag from a post
    public static ApiResponse unassignTagFromPost(String postId, String tagId) {
        ValidationUtils.validateAndThrow(ValidationUtils.isValidObjectId(postId), "Invalid post ID format");
        ValidationUtils.validateAndThrow(ValidationUtils.isValidObjectId(tagId), "Invalid tag ID format");
        String[] endpoint = new String[]{baseUrl + "/" + postId + "/unassign/" + tagId, "DELETE"};
        return FetchFromApi.fetchFromApi(endpoint[0], endpoint[1], null);
    }

    // Get all tags assigned to a post
    public static ApiResponse getAllTagsAssignedToPost(String postId) {
        ValidationUtils.validateAndThrow(ValidationUtils.isValidObjectId(postId), "Invalid post ID format");
        String[] endpoint = new String[]{baseUrl + "/post/" + postId + "/assigned", "GET"};
        return FetchFromApi.fetchFromApi(endpoint[0], endpoint[1], null);
    }

    // Get tags by page and size
    public static ApiResponse getTagsByPage(int page, int size) {
        ValidationUtils.validateAndThrow(page >= 0, "Page number must be 0 or greater");
        ValidationUtils.validateAndThrow(size > 0 && size <= 100, "Page size must be between 1 and 100");
        String[] endpoint = new String[]{baseUrl + "/page?page=" + page + "&size=" + size, "GET"};
        return FetchFromApi.fetchFromApi(endpoint[0], endpoint[1], null);
    }

    // Search tags by name pattern
    public static ApiResponse searchTagsByName(String namePattern) {
        if (namePattern != null && !namePattern.trim().isEmpty()) {
            ValidationUtils.validateAndThrow(namePattern.length() <= 30, "Search pattern must be 30 characters or less");
            ValidationUtils.validateAndThrow(namePattern.matches("^[a-zA-Z0-9_-]*$"), "Search pattern can only contain letters, numbers, underscores, or hyphens");
        }
        String[] endpoint = new String[]{baseUrl + "/search?name=" + (namePattern != null ? namePattern : ""), "GET"};
        return FetchFromApi.fetchFromApi(endpoint[0], endpoint[1], null);
    }

    // Get popular tags (most used)
    public static ApiResponse getPopularTags(int limit) {
        ValidationUtils.validateAndThrow(limit > 0 && limit <= 50, "Limit must be between 1 and 50");
        String[] endpoint = new String[]{baseUrl + "/popular?limit=" + limit, "GET"};
        return FetchFromApi.fetchFromApi(endpoint[0], endpoint[1], null);
    }
}
