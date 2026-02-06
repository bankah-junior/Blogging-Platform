package com.amalitech.bloggingplatform.model.dto.response;

public class ReviewResponse {

    private String id;
    private String postId;
    private String userId;
    private int rating;
    private String feedback;
    private Long createdAt;
    private Long updatedAt;

    public ReviewResponse() {}

    public ReviewResponse(String id, String postId, String userId, int rating, String feedback, Long createdAt, Long updatedAt) {
        this.id = id;
        this.postId = postId;
        this.userId = userId;
        this.rating = rating;
        this.feedback = feedback;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getPostId() { return postId; }
    public void setPostId(String postId) { this.postId = postId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }
    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }
    public Long getCreatedAt() { return createdAt; }
    public void setCreatedAt(Long createdAt) { this.createdAt = createdAt; }
    public Long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Long updatedAt) { this.updatedAt = updatedAt; }
}
