package com.amalitech.bloggingplatform.utils.apis;

import com.amalitech.bloggingplatform.model.dto.request.*;
import com.amalitech.bloggingplatform.utils.ValidationUtils;
import com.amalitech.bloggingplatform.utils.exceptions.UserInputsException;
import com.google.gson.Gson;

public class PostTagApis {
    private final static String baseUrl = "http://localhost:8080/api/v1/post-tags";
    private static final Gson gson = new Gson();

    private PostTagApis() {
        // Utility class - prevent instantiation
    }

    // Assign a tag to a post
    public static ApiResponse assignTagToPost(String postId, String tagId) {
        ValidationUtils.validateAndThrow(ValidationUtils.isValidObjectId(postId), "Invalid post ID format");
        ValidationUtils.validateAndThrow(ValidationUtils.isValidObjectId(tagId), "Invalid tag ID format");
        String[] endpoint = new String[]{baseUrl + "/" + postId + "/assign/" + tagId, "POST"};
        return FetchFromApi.fetchFromApi(endpoint[0], endpoint[1], null);
    }

    // Create a post tag
    public static ApiResponse createPostTag(CreateTagRequest request) {
        if (request == null) {
            throw new UserInputsException("Create tag request cannot be null");
        }
        String[] endpoint = new String[]{baseUrl + "/create", "POST"};
        String requestBody = gson.toJson(request);
        return FetchFromApi.fetchFromApi(endpoint[0], endpoint[1], requestBody);
    }

    // Check if post tag exists
    public static ApiResponse checkIfPostTagExists(String postId, String tagId) {
        ValidationUtils.validateAndThrow(ValidationUtils.isValidObjectId(postId), "Invalid post ID format");
        ValidationUtils.validateAndThrow(ValidationUtils.isValidObjectId(tagId), "Invalid tag ID format");
        String[] endpoint = new String[]{baseUrl + "/" + postId + "/exists/" + tagId, "GET"};
        return FetchFromApi.fetchFromApi(endpoint[0], endpoint[1], null);
    }

    // Get post tag by tag id
    public static ApiResponse getPostTagByTagId(String tagId) {
        ValidationUtils.validateAndThrow(ValidationUtils.isValidObjectId(tagId), "Invalid tag ID format");
        String[] endpoint = new String[]{baseUrl + "/tag/" + tagId, "GET"};
        return FetchFromApi.fetchFromApi(endpoint[0], endpoint[1], null);
    }

    // Get post tag by post id
    public static ApiResponse getPostTagByPostId(String postId) {
        ValidationUtils.validateAndThrow(ValidationUtils.isValidObjectId(postId), "Invalid post ID format");
        String[] endpoint = new String[]{baseUrl + "/post/" + postId, "GET"};
        return FetchFromApi.fetchFromApi(endpoint[0], endpoint[1], null);
    }

    // Get all post tags
    public static ApiResponse getAllPostTags() {
        String[] endpoint = new String[]{baseUrl + "/all", "GET"};
        return FetchFromApi.fetchFromApi(endpoint[0], endpoint[1], null);
    }

    // Unassign tag from a post
    public static ApiResponse unassignTagFromPost(String postId, String tagId) {
        ValidationUtils.validateAndThrow(ValidationUtils.isValidObjectId(postId), "Invalid post ID format");
        ValidationUtils.validateAndThrow(ValidationUtils.isValidObjectId(tagId), "Invalid tag ID format");
        String[] endpoint = new String[]{baseUrl + "/" + postId + "/unassign/" + tagId, "DELETE"};
        return FetchFromApi.fetchFromApi(endpoint[0], endpoint[1], null);
    }

    // Unassign all tags from post by post id
    public static ApiResponse unassignAllTagsFromPost(String postId) {
        ValidationUtils.validateAndThrow(ValidationUtils.isValidObjectId(postId), "Invalid post ID format");
        String[] endpoint = new String[]{baseUrl + "/" + postId + "/unassign-all", "DELETE"};
        return FetchFromApi.fetchFromApi(endpoint[0], endpoint[1], null);
    }
}
