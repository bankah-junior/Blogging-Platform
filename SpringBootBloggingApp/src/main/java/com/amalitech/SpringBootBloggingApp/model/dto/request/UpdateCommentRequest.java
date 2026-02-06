package com.amalitech.SpringBootBloggingApp.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UpdateCommentRequest {
    @NotBlank(message = "Comment ID is required")
    private String id;
    @NotBlank(message = "Content is required")
    @Size(min = 1, max = 1000)
    private String content;

    public UpdateCommentRequest() {}
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
