package com.amalitech.bloggingplatform.utils.apis;

import com.amalitech.bloggingplatform.model.dto.request.*;

/**
 * Unified API client interface for the blogging platform.
 * Provides a single entry point for all API operations.
 */
public class ApiClient {
    
    // User operations
    public static ApiResponse getUserById(String id) {
        return UserApis.getUserById(id);
    }
    
    public static ApiResponse createUser(CreateUserRequest request) {
        return UserApis.createUser(request);
    }
    
    public static ApiResponse updateUser(String id, UpdateUserDetailRequest request) {
        return UserApis.updateUserById(id, request);
    }
    
    public static ApiResponse deleteUser(String id) {
        return UserApis.deleteUserById(id);
    }
    
    public static ApiResponse loginUser(LoginRequest request) {
        return UserApis.loginUser(request);
    }
    
    public static ApiResponse registerUser(RegisterRequest request) {
        return UserApis.registerUser(request);
    }
    
    public static ApiResponse getAllUsers(int page, int size) {
        return UserApis.getAllUsers(page, size);
    }

    public static ApiResponse updateUserDetails(String id, UpdateUserDetailRequest request) {
        return UserApis.updateUserById(id, request);
    }

    public static ApiResponse changeUserPasswordById(String userId, UpdatePasswordRequest request) {
        return UserApis.changeUserPasswordById(userId, request);
    }

    public static ApiResponse getUserByEmail(String email) {
        return UserApis.getUserByEmail(email);
    }
    
    // Post operations
    public static ApiResponse createPost(CreatePostRequest request) {
        return PostApis.createPost(request);
    }
    
    public static ApiResponse getPostById(String postId) {
        return PostApis.getPostById(postId);
    }
    
    public static ApiResponse updatePost(String postId, UpdatePostRequest request) {
        return PostApis.updatePost(request);
    }
    
    public static ApiResponse deletePost(String postId) {
        return PostApis.deletePostById(postId);
    }
    
    public static ApiResponse getAllPosts(int page, int size) {
        return PostApis.getAllPosts(page, size);
    }
    
    public static ApiResponse getPostsByPage(int page, int size) {
        return PostApis.getPostsByPage(page, size);
    }
    
    public static ApiResponse searchPostsByTitle(String title) {
        return PostApis.searchPostByTitle(title);
    }
    
    public static ApiResponse getPostsByAuthor(String authorId) {
        return PostApis.getPostsByAuthorId(authorId);
    }
    
    // Comment operations
    public static ApiResponse createComment(CreateCommentRequest request) {
        return CommentApis.createComment(request);
    }
    
    public static ApiResponse getCommentById(String commentId) {
        return CommentApis.getCommentById(commentId);
    }
    
    public static ApiResponse updateComment(String commentId, UpdateCommentRequest request) {
        return CommentApis.updateComment(request);
    }
    
    public static ApiResponse deleteComment(String commentId) {
        return CommentApis.deleteCommentById(commentId);
    }
    
    public static ApiResponse getCommentsByPost(String postId) {
        return CommentApis.getCommentByPostId(postId);
    }
    
    public static ApiResponse getCommentsByPostPaginated(String postId, int page, int size) {
        return CommentApis.getCommentsByPostIdPaginated(postId, page, size);
    }
    
    // Tag operations
    public static ApiResponse createTag(CreateTagRequest request) {
        return TagApis.createTag(request);
    }
    
    public static ApiResponse getTagById(String tagId) {
        return TagApis.getTagById(tagId);
    }
    
    public static ApiResponse getTagByName(String tagName) {
        return TagApis.getTagByName(tagName);
    }
    
    public static ApiResponse updateTag(String tagId, CreateTagRequest request) {
        return TagApis.updateTag(tagId, request);
    }
    
    public static ApiResponse deleteTag(String tagId) {
        return TagApis.deleteTagById(tagId);
    }
    
    public static ApiResponse getAllTags() {
        return TagApis.getAllTags();
    }
    
    public static ApiResponse assignTagToPost(String postId, String tagId) {
        return TagApis.assignTagToPost(postId, tagId);
    }
    
    public static ApiResponse unassignTagFromPost(String postId, String tagId) {
        return TagApis.unassignTagFromPost(postId, tagId);
    }
    
    // Review operations
    public static ApiResponse createReview(CreateReviewRequest request) {
        return ReviewApis.createReview(request);
    }
    
    public static ApiResponse getReviewById(String reviewId) {
        return ReviewApis.getReviewById(reviewId);
    }
    
    public static ApiResponse updateReview(String reviewId, UpdateReviewRequest request) {
        return ReviewApis.updateReview(request);
    }
    
    public static ApiResponse deleteReview(String reviewId) {
        return ReviewApis.deleteReviewById(reviewId);
    }
    
    public static ApiResponse getReviewsByPost(String postId) {
        return ReviewApis.getReviewsByPostId(postId);
    }
    
    public static ApiResponse getReviewsByUser(String userId) {
        return ReviewApis.getReviewsByUserId(userId);
    }
    
    public static ApiResponse getAverageRatingForPost(String postId) {
        return ReviewApis.getAverageRatingForPost(postId);
    }
    
    // Post-Tag relationship operations
    public static ApiResponse checkPostTagExists(String postId, String tagId) {
        return PostTagApis.checkIfPostTagExists(postId, tagId);
    }
    
    public static ApiResponse getPostTagsByPost(String postId) {
        return PostTagApis.getPostTagByPostId(postId);
    }
    
    public static ApiResponse getPostTagsByTag(String tagId) {
        return PostTagApis.getPostTagByTagId(tagId);
    }
    
    public static ApiResponse unassignAllTagsFromPost(String postId) {
        return PostTagApis.unassignAllTagsFromPost(postId);
    }
    
    private ApiClient() {
        // Utility class - prevent instantiation
    }
}
