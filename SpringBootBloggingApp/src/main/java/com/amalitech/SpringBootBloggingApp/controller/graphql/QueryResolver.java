package com.amalitech.SpringBootBloggingApp.controller.graphql;

import com.amalitech.SpringBootBloggingApp.model.dto.response.UserResponse;
import com.amalitech.SpringBootBloggingApp.model.entity.Comment;
import com.amalitech.SpringBootBloggingApp.model.entity.Post;
import com.amalitech.SpringBootBloggingApp.model.entity.Review;
import com.amalitech.SpringBootBloggingApp.model.entity.Tag;
import com.amalitech.SpringBootBloggingApp.service.*;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class QueryResolver {

    private final PostService postService;
    private final UserService userService;
    private final CommentService commentService;
    private final ReviewService reviewService;
    private final TagService tagService;

    public QueryResolver(PostService postService, UserService userService, CommentService commentService, ReviewService reviewService, TagService tagService) {
        this.postService = postService;
        this.userService = userService;
        this.commentService = commentService;
        this.reviewService = reviewService;
        this.tagService = tagService;
    }

    @QueryMapping
    public List<Post> posts() {
        return postService.getAll();
    }

    @QueryMapping
    public List<UserResponse> users() {
        return userService.getAll();
    }

    @QueryMapping
    public List<Comment> comments() {
        return commentService.getAll();
    }

    @QueryMapping
    public List<Tag> tags() {
        return tagService.getAll();
    }
    
    @QueryMapping
    public List<Review> reviews() {
        return reviewService.getAll();
    }

    @QueryMapping
    public Post postById(@Argument String id) {
        return postService.getById(id);
    }
}
