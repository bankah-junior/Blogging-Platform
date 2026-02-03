package com.amalitech.bloggingplatform.model.dto.request;

public class UpdateReviewRequest {
    private String id;
    private Integer rating;
    private String feedback;

    public UpdateReviewRequest() {}
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }
}
