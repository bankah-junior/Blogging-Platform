package com.amalitech.bloggingplatform.utils.apis;

import com.amalitech.bloggingplatform.model.dto.request.*;
import com.amalitech.bloggingplatform.utils.ValidationUtils;
import com.amalitech.bloggingplatform.utils.exceptions.UserInputsException;
import com.google.gson.Gson;

public class ReviewApis {
    private static final String baseUrl = "http://localhost:8080/api/v1/reviews";
    private static final Gson gson = new Gson();

    private ReviewApis() {
        // Utility class - prevent instantiation
    }

    // Create a review
    public static ApiResponse createReview(CreateReviewRequest request) {
        if (request == null) {
            throw new UserInputsException("Create review request cannot be null");
        }
        String[] endpoint = new String[]{baseUrl + "/create", "POST"};
        String requestBody = gson.toJson(request);
        return FetchFromApi.fetchFromApi(endpoint[0], endpoint[1], requestBody);
    }

    // Update a review
    public static ApiResponse updateReviewById(String reviewId, UpdateReviewRequest request) {
        ValidationUtils.validateAndThrow(ValidationUtils.isValidObjectId(reviewId), "Invalid review ID format");
        if (request == null) {
            throw new UserInputsException("Update review request cannot be null");
        }
        String[] endpoint = new String[]{baseUrl + "/" + reviewId, "PUT"};
        String requestBody = gson.toJson(request);
        return FetchFromApi.fetchFromApi(endpoint[0], endpoint[1], requestBody);
    }

    // Update a review
    public static ApiResponse updateReview(UpdateReviewRequest request) {
        if (request == null) {
            throw new UserInputsException("Update review request cannot be null");
        }
        String[] endpoint = new String[]{baseUrl + "/update", "PUT"};
        String requestBody = gson.toJson(request);
        return FetchFromApi.fetchFromApi(endpoint[0], endpoint[1], requestBody);
    }

    // Get all reviews
    public static ApiResponse getAllReviews() {
        String[] endpoint = new String[]{baseUrl, "GET"};
        return FetchFromApi.fetchFromApi(endpoint[0], endpoint[1], null);
    }

    // Get review by id
    public static ApiResponse getReviewById(String reviewId) {
        ValidationUtils.validateAndThrow(ValidationUtils.isValidObjectId(reviewId), "Invalid review ID format");
        String[] endpoint = new String[]{baseUrl + "/" + reviewId, "GET"};
        return FetchFromApi.fetchFromApi(endpoint[0], endpoint[1], null);
    }

    // Get reviews by post id
    public static ApiResponse getReviewsByPostId(String postId) {
        ValidationUtils.validateAndThrow(ValidationUtils.isValidObjectId(postId), "Invalid post ID format");
        String[] endpoint = new String[]{baseUrl + "/post/" + postId, "GET"};
        return FetchFromApi.fetchFromApi(endpoint[0], endpoint[1], null);
    }

    // Get reviews by user id
    public static ApiResponse getReviewsByUserId(String userId) {
        ValidationUtils.validateAndThrow(ValidationUtils.isValidObjectId(userId), "Invalid user ID format");
        String[] endpoint = new String[]{baseUrl + "/user/" + userId, "GET"};
        return FetchFromApi.fetchFromApi(endpoint[0], endpoint[1], null);
    }

    // Get average rating for a post
    public static ApiResponse getAverageRatingForPost(String postId) {
        ValidationUtils.validateAndThrow(ValidationUtils.isValidObjectId(postId), "Invalid post ID format");
        String[] endpoint = new String[]{baseUrl + "/post/" + postId + "/average-rating", "GET"};
        return FetchFromApi.fetchFromApi(endpoint[0], endpoint[1], null);
    }

    // Delete review by id
    public static ApiResponse deleteReviewById(String reviewId) {
        ValidationUtils.validateAndThrow(ValidationUtils.isValidObjectId(reviewId), "Invalid review ID format");
        String[] endpoint = new String[]{baseUrl + "/" + reviewId, "DELETE"};
        return FetchFromApi.fetchFromApi(endpoint[0], endpoint[1], null);
    }

    // Get reviews by rating
    public static ApiResponse getReviewsByRating(int rating) {
        ValidationUtils.validateAndThrow(ValidationUtils.isValidRating(rating), "Rating must be between 1 and 5");
        String[] endpoint = new String[]{baseUrl + "/rating/" + rating, "GET"};
        return FetchFromApi.fetchFromApi(endpoint[0], endpoint[1], null);
    }

    // Get reviews by rating range
    public static ApiResponse getReviewsByRatingRange(int minRating, int maxRating) {
        ValidationUtils.validateAndThrow(ValidationUtils.isValidRating(minRating), "Minimum rating must be between 1 and 5");
        ValidationUtils.validateAndThrow(ValidationUtils.isValidRating(maxRating), "Maximum rating must be between 1 and 5");
        ValidationUtils.validateAndThrow(minRating <= maxRating, "Minimum rating cannot be greater than maximum rating");
        String[] endpoint = new String[]{baseUrl + "/rating/range?min=" + minRating + "&max=" + maxRating, "GET"};
        return FetchFromApi.fetchFromApi(endpoint[0], endpoint[1], null);
    }

    // Get reviews by page and size
    public static ApiResponse getReviewsByPage(int page, int size) {
        ValidationUtils.validateAndThrow(page >= 0, "Page number must be 0 or greater");
        ValidationUtils.validateAndThrow(size > 0 && size <= 100, "Page size must be between 1 and 100");
        String[] endpoint = new String[]{baseUrl + "/page?page=" + page + "&size=" + size, "GET"};
        return FetchFromApi.fetchFromApi(endpoint[0], endpoint[1], null);
    }

    // Get reviews by post id with pagination
    public static ApiResponse getReviewsByPostIdPaginated(String postId, int page, int size) {
        ValidationUtils.validateAndThrow(ValidationUtils.isValidObjectId(postId), "Invalid post ID format");
        ValidationUtils.validateAndThrow(page >= 0, "Page number must be 0 or greater");
        ValidationUtils.validateAndThrow(size > 0 && size <= 100, "Page size must be between 1 and 100");
        String[] endpoint = new String[]{baseUrl + "/post/" + postId + "?page=" + page + "&size=" + size, "GET"};
        return FetchFromApi.fetchFromApi(endpoint[0], endpoint[1], null);
    }
}
