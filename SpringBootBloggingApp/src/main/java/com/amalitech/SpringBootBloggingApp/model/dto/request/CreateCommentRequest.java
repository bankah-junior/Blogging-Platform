package com.amalitech.SpringBootBloggingApp.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateCommentRequest {
    @NotBlank(message = "Post ID is required")
    private String postId;
    @NotBlank(message = "User ID is required")
    private String userId;
    @NotBlank(message = "Content is required")
    @Size(min = 1, max = 1000)
    private String content;

    public CreateCommentRequest() {}
    public String getPostId() { return postId; }
    public void setPostId(String postId) { this.postId = postId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
