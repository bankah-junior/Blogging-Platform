package com.amalitech.bloggingplatform.model.dto.request;

public class CreatePostRequest {
    private String authorId;
    private String title;
    private String content;
    private Boolean published;

    public CreatePostRequest() {}
    public String getAuthorId() { return authorId; }
    public void setAuthorId(String authorId) { this.authorId = authorId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Boolean getPublished() { return published; }
    public void setPublished(Boolean published) { this.published = published; }
}
