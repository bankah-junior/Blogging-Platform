package com.amalitech.SpringBootBloggingApp.controller.graphql;

import com.amalitech.SpringBootBloggingApp.model.dto.request.*;
import com.amalitech.SpringBootBloggingApp.model.dto.response.UserResponse;
import com.amalitech.SpringBootBloggingApp.model.entity.Comment;
import com.amalitech.SpringBootBloggingApp.model.entity.Post;
import com.amalitech.SpringBootBloggingApp.model.entity.Review;
import com.amalitech.SpringBootBloggingApp.model.entity.Tag;
import com.amalitech.SpringBootBloggingApp.model.entity.User;
import com.amalitech.SpringBootBloggingApp.service.CommentService;
import com.amalitech.SpringBootBloggingApp.service.PostService;
import com.amalitech.SpringBootBloggingApp.service.ReviewService;
import com.amalitech.SpringBootBloggingApp.service.TagService;
import com.amalitech.SpringBootBloggingApp.service.UserService;
import com.amalitech.SpringBootBloggingApp.util.JwtUtilManual;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class MutationResolver {

    private final PostService postService;
    private final UserService userService;
    private final CommentService commentService;
    private final ReviewService reviewService;
    private final TagService tagService;

    public MutationResolver(PostService postService, UserService userService, CommentService commentService, ReviewService reviewService, TagService tagService) {
        this.postService = postService;
        this.userService = userService;
        this.commentService = commentService;
        this.reviewService = reviewService;
        this.tagService = tagService;
    }

    @MutationMapping
    public Comment addComment(@Argument CreateCommentRequest input) {
        UserResponse userResponse = userService.getById(input.getUserId());
        Post post = postService.getById(input.getPostId());
        
        User user = new User(
                userResponse.getId(),
                userResponse.getUsername(),
                userResponse.getEmail(),
                null,
                userResponse.getCreatedAt(),
                userResponse.getUpdatedAt()
        );

        Comment comment = new Comment(
                null,
                post,
                user,
                input.getContent(),
                System.currentTimeMillis(),
                System.currentTimeMillis()
        );

        return commentService.create(comment);
    }

    @MutationMapping
    public Review addReview(@Argument CreateReviewRequest input) {
        UserResponse userResponse = userService.getById(input.getUserId());
        Post post = postService.getById(input.getPostId());
        
        User user = new User(
                userResponse.getId(),
                userResponse.getUsername(),
                userResponse.getEmail(),
                null,
                userResponse.getCreatedAt(),
                userResponse.getUpdatedAt()
        );

        Review review = new Review(
                null,
                post,
                user,
                input.getRating(),
                input.getFeedback(),
                System.currentTimeMillis(),
                System.currentTimeMillis()
        );

        return reviewService.create(review);
    }

    @MutationMapping
    public Tag createTag(@Argument CreateTagRequest input) {
        Tag tag = new Tag(
                null,
                input.getName()
        );

        return tagService.create(tag);
    }

    @MutationMapping
    public Post createPost(@Argument CreatePostRequest input) {

        UserResponse author = userService.getById(input.getAuthorId());

        if (author == null) {
            throw new IllegalArgumentException(
                    "User not found with id: " + input.getAuthorId()
            );
        }

        Post post = new Post(
                null,
                new User(
                        author.getId(),
                        author.getUsername(),
                        author.getEmail(),
                        null,
                        author.getCreatedAt(),
                        author.getUpdatedAt()
                ),
                input.getTitle(),
                input.getContent(),
                false,
                System.currentTimeMillis(),
                System.currentTimeMillis(),
                List.of()
        );

        return postService.create(post);
    }

    @MutationMapping
    public UserResponse updateUser(@Argument UpdateUserDetailRequest input) {
        UserResponse existingUser = userService.getById(input.getId());
        
        UpdateUserRequest user = new UpdateUserRequest(
                input.getId(),
                input.getUsername() != null ? input.getUsername() : existingUser.getUsername(),
                input.getEmail() != null ? input.getEmail() : existingUser.getEmail(),
                null
        );

        UserResponse updatedUser = userService.update(user);
        String token = JwtUtilManual.generateToken(updatedUser.getId(), updatedUser.getEmail());
        return new UserResponse(
                updatedUser.getId(),
                updatedUser.getUsername(),
                updatedUser.getEmail(),
                updatedUser.getCreatedAt(),
                updatedUser.getUpdatedAt(),
                token
        );
    }
}

