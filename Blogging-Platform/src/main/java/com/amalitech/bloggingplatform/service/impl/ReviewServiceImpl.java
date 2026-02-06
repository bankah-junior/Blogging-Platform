package com.amalitech.bloggingplatform.service.impl;

import com.amalitech.bloggingplatform.model.dto.request.CreateReviewRequest;
import com.amalitech.bloggingplatform.model.dto.response.ReviewResponse;
import com.amalitech.bloggingplatform.model.entity.Review;
import com.amalitech.bloggingplatform.service.ReviewService;
import com.amalitech.bloggingplatform.utils.apis.ApiClient;
import com.amalitech.bloggingplatform.utils.apis.ApiResponse;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReviewServiceImpl implements ReviewService {

    private final Gson gson = new Gson();

    @Override
    public Review create(Review review) {
        if (review == null) {
            return null;
        }
        CreateReviewRequest request = new CreateReviewRequest();
        request.setPostId(review.getPostId());
        request.setUserId(review.getUserId());
        request.setRating(review.getRating());
        request.setFeedback(review.getFeedback());

        ApiResponse response = ApiClient.createReview(request);
        return parseSingleReviewFromResponse(response);
    }

    @Override
    public boolean delete(String reviewId) {
        if (reviewId == null) {
            return false;
        }
        ApiResponse response = ApiClient.deleteReview(reviewId);
        return isSuccess(response);
    }

    @Override
    public List<Review> getByPost(String postId) {
        if (postId == null) {
            return Collections.emptyList();
        }
        ApiResponse response = ApiClient.getReviewsByPost(postId);
        List<ReviewResponse> dtos = parseReviewListFromResponse(response);
        return toReviewEntities(dtos);
    }

    @Override
    public double getAverageRatingForPost(String postId) {
        if (postId == null) {
            return 0.0;
        }
        ApiResponse response = ApiClient.getAverageRatingForPost(postId);
        if (!isSuccess(response) || response.getData() == null) {
            return 0.0;
        }
        JsonElement data = response.getData();
        if (data.isJsonPrimitive() && data.getAsJsonPrimitive().isNumber()) {
            return data.getAsDouble();
        }
        return 0.0;
    }

    // ---------- Helpers ----------

    private boolean isSuccess(ApiResponse response) {
        return response != null && "success".equalsIgnoreCase(response.getStatus());
    }

    private Review parseSingleReviewFromResponse(ApiResponse response) {
        if (!isSuccess(response) || response.getData() == null) {
            return null;
        }
        ReviewResponse dto = gson.fromJson(response.getData(), ReviewResponse.class);
        if (dto == null) {
            return null;
        }
        // Extract IDs from nested objects if present
        JsonElement json = response.getData();
        String postId = extractPostIdFromJson(json);
        String userId = extractUserIdFromJson(json);
        if (postId != null) {
            dto.setPostId(postId);
        }
        if (userId != null) {
            dto.setUserId(userId);
        }
        Review r = new Review();
        r.setId(dto.getId());
        r.setPostId(dto.getPostId());
        r.setUserId(dto.getUserId());
        r.setRating(dto.getRating());
        r.setFeedback(dto.getFeedback());
        r.setCreatedAt(dto.getCreatedAt());
        r.setUpdatedAt(dto.getUpdatedAt());
        return r;
    }

    private List<ReviewResponse> parseReviewListFromResponse(ApiResponse response) {
        if (!isSuccess(response) || response.getData() == null) {
            return Collections.emptyList();
        }
        JsonElement data = response.getData();
        List<ReviewResponse> dtos = new ArrayList<>();
        
        if (data.isJsonArray()) {
            for (JsonElement elem : data.getAsJsonArray()) {
                ReviewResponse dto = gson.fromJson(elem, ReviewResponse.class);
                if (dto != null) {
                    // Extract IDs from nested objects if present
                    String postId = extractPostIdFromJson(elem);
                    String userId = extractUserIdFromJson(elem);
                    if (postId != null) {
                        dto.setPostId(postId);
                    }
                    if (userId != null) {
                        dto.setUserId(userId);
                    }
                    dtos.add(dto);
                }
            }
        }
        return dtos;
    }

    private String extractPostIdFromJson(JsonElement json) {
        if (json == null || !json.isJsonObject()) {
            return null;
        }
        com.google.gson.JsonObject obj = json.getAsJsonObject();
        // Check if there's a nested "post" object
        if (obj.has("post") && obj.get("post").isJsonObject()) {
            com.google.gson.JsonObject post = obj.get("post").getAsJsonObject();
            if (post.has("id")) {
                return post.get("id").getAsString();
            }
        }
        // If postId is already present as a string, return it
        if (obj.has("postId") && obj.get("postId").isJsonPrimitive()) {
            return obj.get("postId").getAsString();
        }
        return null;
    }

    private String extractUserIdFromJson(JsonElement json) {
        if (json == null || !json.isJsonObject()) {
            return null;
        }
        com.google.gson.JsonObject obj = json.getAsJsonObject();
        // Check if there's a nested "user" object
        if (obj.has("user") && obj.get("user").isJsonObject()) {
            com.google.gson.JsonObject user = obj.get("user").getAsJsonObject();
            if (user.has("id")) {
                return user.get("id").getAsString();
            }
        }
        // If userId is already present as a string, return it
        if (obj.has("userId") && obj.get("userId").isJsonPrimitive()) {
            return obj.get("userId").getAsString();
        }
        return null;
    }

    private List<Review> toReviewEntities(List<ReviewResponse> dtos) {
        if (dtos == null || dtos.isEmpty()) {
            return Collections.emptyList();
        }
        return dtos.stream().map(dto -> {
            Review r = new Review();
            r.setId(dto.getId());
            r.setPostId(dto.getPostId());
            r.setUserId(dto.getUserId());
            r.setRating(dto.getRating());
            r.setFeedback(dto.getFeedback());
            r.setCreatedAt(dto.getCreatedAt());
            r.setUpdatedAt(dto.getUpdatedAt());
            return r;
        }).toList();
    }
}