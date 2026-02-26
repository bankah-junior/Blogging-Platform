package com.amalitech.SpringBootBloggingApp.model.dto.response;

public class PostViewStats {
    private final String postId;
    private final String title;
    private final long viewCount;

    public PostViewStats(String postId, String title, long viewCount) {
        this.postId = postId;
        this.title = title;
        this.viewCount = viewCount;
    }

    public String getPostId()   { return postId; }
    public String getTitle()    { return title; }
    public long   getViewCount(){ return viewCount; }
}
