package com.amalitech.SpringBootBloggingApp.model.dto.response;

public class PostResponse {
    private String id;
    private String authorId;
    private String title;
    private String content;
    private boolean published;
    private Long createdAt;
    private Long updatedAt;

    public PostResponse() {}
    public PostResponse(String id, String authorId, String title, String content, boolean published, Long createdAt, Long updatedAt) {
        this.id = id;
        this.authorId = authorId;
        this.title = title;
        this.content = content;
        this.published = published;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getAuthorId() { return authorId; }
    public void setAuthorId(String authorId) { this.authorId = authorId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public boolean isPublished() { return published; }
    public void setPublished(boolean published) { this.published = published; }
    public Long getCreatedAt() { return createdAt; }
    public void setCreatedAt(Long createdAt) { this.createdAt = createdAt; }
    public Long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Long updatedAt) { this.updatedAt = updatedAt; }
}
