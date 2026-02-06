package com.amalitech.SpringBootBloggingApp.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UpdatePostRequest {
    @NotBlank(message = "Post ID is required")
    private String id;
    @NotBlank(message = "Title is required")
    @Size(min = 1, max = 200)
    private String title;
    @NotBlank(message = "Content is required")
    @Size(max = 10000)
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
