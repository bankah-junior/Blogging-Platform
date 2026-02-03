package com.amalitech.bloggingplatform.model.entity;

public class Comment {

    private String id;
    private String postId;
    private String userId;
    private String content;
    private Long createdAt;
    private Long updatedAt;

    public Comment() {}

    public Comment(String id, String postId, String userId, String content, Long createdAt, Long updatedAt) {
        this.id = id;
        this.postId = postId;
        this.userId = userId;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters & Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getPostId() { return postId; }
    public void setPostId(String postId) { this.postId = postId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Long getCreatedAt() { return createdAt; }
    public void setCreatedAt(Long createdAt) { this.createdAt = createdAt; }

    public Long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Long updatedAt) { this.updatedAt = updatedAt; }

    public String getCommentId() { return id; };
    public void setCommentId(String commentId) {
        this.id = commentId;
    }
}
