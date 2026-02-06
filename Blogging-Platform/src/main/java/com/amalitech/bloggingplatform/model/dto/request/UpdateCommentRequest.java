package com.amalitech.bloggingplatform.model.dto.request;

public class UpdateCommentRequest {
    private String id;
    private String content;

    public UpdateCommentRequest() {}
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
