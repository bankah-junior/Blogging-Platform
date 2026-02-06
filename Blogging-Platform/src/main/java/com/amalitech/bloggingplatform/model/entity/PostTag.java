package com.amalitech.bloggingplatform.model.entity;

public class PostTag {

    private String id;
    private String postId;
    private String tagId;

    public PostTag() {}

    public PostTag(String postId, String tagId) {
        this.postId = postId;
        this.tagId = tagId;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getPostId() { return postId; }
    public void setPostId(String postId) { this.postId = postId; }

    public String getTagId() { return tagId; }
    public void setTagId(String tagId) { this.tagId = tagId; }
}

