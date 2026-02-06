package com.amalitech.bloggingplatform.model.entity;

import java.util.List;

public class Post {

    private String id;
    private String authorId;
    private String title;
    private String content;
    private boolean published;
    private Long createdAt;
    private Long updatedAt;
    private List<Review> reviews;

    public Post() {}

    public Post(String id, String authorId, String title, String content,
                boolean published, Long createdAt, Long updatedAt) {
        this.id = id;
        this.authorId = authorId;
        this.title = title;
        this.content = content;
        this.published = published;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters & Setters
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

    public List<Review> getReviews() { return reviews; }
    public void setReviews(List<Review> reviews) { this.reviews = reviews; }

    public String getPostId() { return id; }
    public void setPostId(String postId) {
        this.id = postId;
    }
}