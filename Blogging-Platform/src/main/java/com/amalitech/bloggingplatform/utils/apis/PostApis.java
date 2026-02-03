package com.amalitech.bloggingplatform.utils.apis;

import com.amalitech.bloggingplatform.model.dto.request.*;
import com.amalitech.bloggingplatform.utils.ValidationUtils;
import com.amalitech.bloggingplatform.utils.exceptions.UserInputsException;
import com.google.gson.Gson;

public class PostApis {
    private static final String baseUrl = "http://localhost:8080/api/v1/posts";
    private static final Gson gson = new Gson();

    private PostApis() {
        // Utility class - prevent instantiation
    }

    // Create a post
    public static ApiResponse createPost(CreatePostRequest request) {
        if (request == null) {
            throw new UserInputsException("Create post request cannot be null");
        }
        String[] endpoint = new String[]{baseUrl + "/create", "POST"};
        String requestBody = gson.toJson(request);
        return FetchFromApi.fetchFromApi(endpoint[0], endpoint[1], requestBody);
    }

    // Update a post
    public static ApiResponse updatePostById(String postId, UpdatePostRequest request) {
        ValidationUtils.validateAndThrow(ValidationUtils.isValidObjectId(postId), "Invalid post ID format");
        if (request == null) {
            throw new UserInputsException("Update post request cannot be null");
        }
        String[] endpoint = new String[]{baseUrl + "/" + postId, "PUT"};
        String requestBody = gson.toJson(request);
        return FetchFromApi.fetchFromApi(endpoint[0], endpoint[1], requestBody);
    }

    // Update a post
    public static ApiResponse updatePost(UpdatePostRequest request) {
        if (request == null) {
            throw new UserInputsException("Update post request cannot be null");
        }
        String[] endpoint = new String[]{baseUrl + "/update", "PUT"};
        String requestBody = gson.toJson(request);
        return FetchFromApi.fetchFromApi(endpoint[0], endpoint[1], requestBody);
    }

    // Get a post by id
    public static ApiResponse getPostById(String postId) {
        ValidationUtils.validateAndThrow(ValidationUtils.isValidObjectId(postId), "Invalid post ID format");
        String[] endpoint = new String[]{baseUrl + "/" + postId, "GET"};
        return FetchFromApi.fetchFromApi(endpoint[0], endpoint[1], null);
    }

    // Get all posts
    public static ApiResponse getAllPosts(int page, int size) {
        ValidationUtils.validateAndThrow(page >= 0, "Page number must be 0 or greater");
        ValidationUtils.validateAndThrow(size > 0 && size <= 100, "Page size must be between 1 and 100");
        String[] endpoint = new String[]{baseUrl + "/all?page=" + page + "&size=" + size, "GET"};
        return FetchFromApi.fetchFromApi(endpoint[0], endpoint[1], null);
    }

    // Get all posts sorted by title
    public static ApiResponse getAllPostsSortedByTitle() {
        String[] endpoint = new String[]{baseUrl + "/sort/title", "GET"};
        return FetchFromApi.fetchFromApi(endpoint[0], endpoint[1], null);
    }

    // Get all posts sorted by date
    public static ApiResponse getAllPostsSortedByDate() {
        String[] endpoint = new String[]{baseUrl + "/sort/date", "GET"};
        return FetchFromApi.fetchFromApi(endpoint[0], endpoint[1], null);
    }

    // Get all sorted posts
    public static ApiResponse getAllSortedPosts() {
        String[] endpoint = new String[]{baseUrl + "/sort/all", "GET"};
        return FetchFromApi.fetchFromApi(endpoint[0], endpoint[1], null);
    }

    // Search post by title
    public static ApiResponse searchPostByTitle(String title) {
        ValidationUtils.validateAndThrow(ValidationUtils.isNotBlank(title), "Search title cannot be empty");
        ValidationUtils.validateAndThrow(title.length() <= 150, "Search title must be 150 characters or less");
        String[] endpoint = new String[]{baseUrl + "/search?title=" + title, "GET"};
        return FetchFromApi.fetchFromApi(endpoint[0], endpoint[1], null);
    }

    // Search post by content
    public static ApiResponse searchPostByContent(String content) {
        ValidationUtils.validateAndThrow(ValidationUtils.isNotBlank(content), "Search content cannot be empty");
        ValidationUtils.validateAndThrow(content.length() <= 1000, "Search content must be 1000 characters or less");
        String[] endpoint = new String[]{baseUrl + "/search?content=" + content, "GET"};
        return FetchFromApi.fetchFromApi(endpoint[0], endpoint[1], null);
    }

    // Search post by tag name
    public static ApiResponse searchPostByTagName(String tagName) {
        ValidationUtils.validateAndThrow(ValidationUtils.isValidTagName(tagName), "Invalid tag name format");
        String[] endpoint = new String[]{baseUrl + "/search/tag?tagName=" + tagName, "GET"};
        return FetchFromApi.fetchFromApi(endpoint[0], endpoint[1], null);
    }

    // Get posts by published status
    public static ApiResponse getPostsByPublishedStatus(boolean isPublished) {
        String[] endpoint = new String[]{baseUrl + "/search/published?published=" + isPublished, "GET"};
        return FetchFromApi.fetchFromApi(endpoint[0], endpoint[1], null);
    }

    // Get posts by author id
    public static ApiResponse getPostsByAuthorId(String authorId) {
        ValidationUtils.validateAndThrow(ValidationUtils.isValidObjectId(authorId), "Invalid author ID format");
        String[] endpoint = new String[]{baseUrl + "/search/author?authorId=" + authorId, "GET"};
        return FetchFromApi.fetchFromApi(endpoint[0], endpoint[1], null);
    }

    // Delete a post by id
    public static ApiResponse deletePostById(String postId) {
        ValidationUtils.validateAndThrow(ValidationUtils.isValidObjectId(postId), "Invalid post ID format");
        String[] endpoint = new String[]{baseUrl + "/" + postId, "DELETE"};
        return FetchFromApi.fetchFromApi(endpoint[0], endpoint[1], null);
    }

    // Get posts by page and size
    public static ApiResponse getPostsByPage(int page, int size) {
        ValidationUtils.validateAndThrow(page >= 0, "Page number must be 0 or greater");
        ValidationUtils.validateAndThrow(size > 0 && size <= 100, "Page size must be between 1 and 100");
        String[] endpoint = new String[]{baseUrl + "/page?page=" + page + "&size=" + size, "GET"};
        return FetchFromApi.fetchFromApi(endpoint[0], endpoint[1], null);
    }

    // Get published posts by page and size
    public static ApiResponse getPublishedPostsByPage(int page, int size) {
        ValidationUtils.validateAndThrow(page >= 0, "Page number must be 0 or greater");
        ValidationUtils.validateAndThrow(size > 0 && size <= 100, "Page size must be between 1 and 100");
        String[] endpoint = new String[]{baseUrl + "/published?page=" + page + "&size=" + size, "GET"};
        return FetchFromApi.fetchFromApi(endpoint[0], endpoint[1], null);
    }
}
