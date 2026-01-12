package com.amalitech.bloggingplatform.model;

public class PostTag {

    private String postId;
    private String tagId;

    public PostTag() {}

    public PostTag(String postId, String tagId) {
        this.postId = postId;
        this.tagId = tagId;
    }

    public String getPostId() { return postId; }
    public void setPostId(String postId) { this.postId = postId; }

    public String getTagId() { return tagId; }
    public void setTagId(String tagId) { this.tagId = tagId; }
}

