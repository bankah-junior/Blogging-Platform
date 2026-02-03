package com.amalitech.SpringBootBloggingApp.model.dto.request;

import jakarta.validation.constraints.NotBlank;

public class PostTagRequest {

    @NotBlank(message = "Post ID is required")
    private String postId;

    @NotBlank(message = "Tag ID is required")
    private String tagId;

    public PostTagRequest() {}

    public String getPostId() {
        return postId;
    }
    public void setPostId(String postId) {
        this.postId = postId;
    }
    public String getTagId() {
        return tagId;
    }
    public void setTagId(String tagId) {
        this.tagId = tagId;
    }
}
