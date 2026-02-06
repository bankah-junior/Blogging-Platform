package com.amalitech.bloggingplatform.model.dto.request;

public class PostTagRequest {
    private String postId;
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
