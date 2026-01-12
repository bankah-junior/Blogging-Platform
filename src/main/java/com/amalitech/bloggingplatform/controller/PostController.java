package com.amalitech.bloggingplatform.controller;

import com.amalitech.bloggingplatform.model.Post;
import com.amalitech.bloggingplatform.service.impl.PostServiceImpl;

import java.util.List;

public class  PostController {
    private final PostServiceImpl postService;

    public PostController() {
        this.postService = new PostServiceImpl();
    }

    public List<Post> findAll() {
        return postService.getAll();
    }
}
