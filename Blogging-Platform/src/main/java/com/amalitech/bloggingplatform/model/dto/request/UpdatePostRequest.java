package com.amalitech.bloggingplatform.model.dto.request;

public class UpdatePostRequest {
    private String id;
    private String title;
    private String content;
    private boolean published;

    public UpdatePostRequest() {}
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public boolean isPublished() { return published; }
    public void setPublished(boolean published) { this.published = published; }
}
