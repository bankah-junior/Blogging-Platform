package com.amalitech.bloggingplatform.service.impl;

import com.amalitech.bloggingplatform.model.dto.request.CreateCommentRequest;
import com.amalitech.bloggingplatform.model.dto.response.CommentResponse;
import com.amalitech.bloggingplatform.model.entity.Comment;
import com.amalitech.bloggingplatform.service.CommentService;
import com.amalitech.bloggingplatform.utils.apis.ApiClient;
import com.amalitech.bloggingplatform.utils.apis.ApiResponse;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommentServiceImpl implements CommentService {

    private final Gson gson = new Gson();

    @Override
    public Comment create(Comment comment) {
        if (comment == null) {
            return null;
        }
        CreateCommentRequest request = new CreateCommentRequest();
        request.setPostId(comment.getPostId());
        request.setUserId(comment.getUserId());
        request.setContent(comment.getContent());

        ApiResponse response = ApiClient.createComment(request);
        return parseSingleCommentFromResponse(response);
    }

    @Override
    public boolean delete(String commentId) {
        if (commentId == null) {
            return false;
        }
        ApiResponse response = ApiClient.deleteComment(commentId);
        return isSuccess(response);
    }

    @Override
    public List<Comment> getByPost(String postId) {
        if (postId == null) {
            return Collections.emptyList();
        }
        ApiResponse response = ApiClient.getCommentsByPost(postId);
        List<CommentResponse> dtos = parseCommentListFromResponse(response);
        return toCommentEntities(dtos);
    }

    @Override
    public List<Comment> getByUser(String userId) {
        if (userId == null) {
            return Collections.emptyList();
        }
        // There is a dedicated endpoint in CommentApis for comments by user;
        // once ApiClient exposes it we can switch. For now, return empty list.
        return Collections.emptyList();
    }

    // ---------- Helpers ----------

    private boolean isSuccess(ApiResponse response) {
        return response != null && "success".equalsIgnoreCase(response.getStatus());
    }

    private Comment parseSingleCommentFromResponse(ApiResponse response) {
        if (!isSuccess(response) || response.getData() == null) {
            return null;
        }
        CommentResponse dto = gson.fromJson(response.getData(), CommentResponse.class);
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
        Comment c = new Comment();
        c.setId(dto.getId());
        c.setPostId(dto.getPostId());
        c.setUserId(dto.getUserId());
        c.setContent(dto.getContent());
        c.setCreatedAt(dto.getCreatedAt());
        c.setUpdatedAt(dto.getUpdatedAt());
        return c;
    }

    private List<CommentResponse> parseCommentListFromResponse(ApiResponse response) {
        if (!isSuccess(response) || response.getData() == null) {
            return Collections.emptyList();
        }
        JsonElement data = response.getData();
        List<CommentResponse> dtos = new ArrayList<>();
        
        if (data.isJsonArray()) {
            for (JsonElement elem : data.getAsJsonArray()) {
                CommentResponse dto = gson.fromJson(elem, CommentResponse.class);
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

    private List<Comment> toCommentEntities(List<CommentResponse> dtos) {
        if (dtos == null || dtos.isEmpty()) {
            return Collections.emptyList();
        }
        return dtos.stream().map(dto -> {
            Comment c = new Comment();
            c.setId(dto.getId());
            c.setPostId(dto.getPostId());
            c.setUserId(dto.getUserId());
            c.setContent(dto.getContent());
            c.setCreatedAt(dto.getCreatedAt());
            c.setUpdatedAt(dto.getUpdatedAt());
            return c;
        }).toList();
    }
}