package com.amalitech.bloggingplatform.model.dto.request;

public class CreateReviewRequest {
    private String postId;
    private String userId;
    private Integer rating;
    private String feedback;

    public CreateReviewRequest() {}
    public String getPostId() { return postId; }
    public void setPostId(String postId) { this.postId = postId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }
}
