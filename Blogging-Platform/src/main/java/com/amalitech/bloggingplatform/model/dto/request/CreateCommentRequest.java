package com.amalitech.bloggingplatform.model.dto.request;

public class CreateCommentRequest {
    private String postId;
    private String userId;
    private String content;

    public CreateCommentRequest() {}
    public String getPostId() { return postId; }
    public void setPostId(String postId) { this.postId = postId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
