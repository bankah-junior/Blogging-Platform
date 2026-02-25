package com.amalitech.SpringBootBloggingApp.controller;

import com.amalitech.SpringBootBloggingApp.model.dto.DtoMapper;
import com.amalitech.SpringBootBloggingApp.model.dto.request.CreateCommentRequest;
import com.amalitech.SpringBootBloggingApp.model.dto.response.ApiResponse;
import com.amalitech.SpringBootBloggingApp.model.dto.response.CommentResponse;
import com.amalitech.SpringBootBloggingApp.model.dto.response.PostResponse;
import com.amalitech.SpringBootBloggingApp.model.dto.response.UserResponse;
import com.amalitech.SpringBootBloggingApp.model.entity.Comment;
import com.amalitech.SpringBootBloggingApp.model.entity.Post;
import com.amalitech.SpringBootBloggingApp.service.CommentService;
import com.amalitech.SpringBootBloggingApp.service.PostService;
import com.amalitech.SpringBootBloggingApp.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reader")
@Tag(name = "Reader", description = "Reader operations for consuming and interacting with content")
@SecurityRequirement(name = "Bearer Authentication")
public class ReaderController {

    private final PostService postService;
    private final CommentService commentService;
    private final UserService userService;

    public ReaderController(PostService postService, CommentService commentService, UserService userService) {
        this.postService = postService;
        this.commentService = commentService;
        this.userService = userService;
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('ADMIN', 'AUTHOR', 'READER')")
    @Operation(summary = "Reader dashboard", description = "Get reader dashboard with personalized content")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getReaderDashboard(
            @AuthenticationPrincipal UserDetails userDetails) {
        UserResponse user = userService.getByUsername(userDetails.getUsername());

        // Recent published posts as recommendations (latest 5)
        List<PostResponse> recommended = postService.getByPublishedPaginated(true, 0, 5)
                .getContent().stream()
                .map(DtoMapper::toPostResponse)
                .collect(Collectors.toList());

        // Posts the user has commented on (reading history proxy)
        List<PostResponse> recentlyRead = commentService.getByUser(user.getId()).stream()
                .map(Comment::getPost)
                .filter(p -> p != null)
                .distinct()
                .map(DtoMapper::toPostResponse)
                .collect(Collectors.toList());

        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("username", userDetails.getUsername());
        dashboard.put("message", "Welcome to Reader Dashboard");
        dashboard.put("recommendedPosts", recommended);
        dashboard.put("recentlyRead", recentlyRead);
        return ResponseEntity.ok(ApiResponse.success(dashboard));
    }

    @PostMapping("/posts/{postId}/like")
    @PreAuthorize("hasAnyRole('ADMIN', 'AUTHOR', 'READER')")
    @Operation(summary = "Like post", description = "Like a blog post")
    public ResponseEntity<ApiResponse<Map<String, Object>>> likePost(
            @PathVariable String postId,
            @AuthenticationPrincipal UserDetails userDetails) {
        Post post = postService.getById(postId);
        if (post == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Post not found"));
        }
        // Count existing comments as engagement metric (no dedicated Like entity)
        long likeCount = commentService.getByPost(postId).size();
        Map<String, Object> result = new HashMap<>();
        result.put("postId", postId);
        result.put("likedBy", userDetails.getUsername());
        result.put("totalEngagements", likeCount);
        return ResponseEntity.ok(ApiResponse.success("Post liked", result));
    }

    @PostMapping("/posts/{postId}/comment")
    @PreAuthorize("hasAnyRole('ADMIN', 'AUTHOR', 'READER')")
    @Operation(summary = "Comment on post", description = "Add a comment to a blog post")
    public ResponseEntity<ApiResponse<CommentResponse>> commentOnPost(
            @PathVariable String postId,
            @Valid @RequestBody CreateCommentRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        Post post = postService.getById(postId);
        if (post == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Post not found"));
        }
        UserResponse user = userService.getByUsername(userDetails.getUsername());
        request.setPostId(postId);
        request.setUserId(user.getId());
        Comment created = commentService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Comment added successfully", DtoMapper.toCommentResponse(created)));
    }

    @PostMapping("/posts/{postId}/save")
    @PreAuthorize("hasAnyRole('ADMIN', 'AUTHOR', 'READER')")
    @Operation(summary = "Save post", description = "Save a post to favorites")
    public ResponseEntity<ApiResponse<PostResponse>> savePost(
            @PathVariable String postId,
            @AuthenticationPrincipal UserDetails userDetails) {
        Post post = postService.getById(postId);
        if (post == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Post not found"));
        }
        return ResponseEntity.ok(ApiResponse.success(
                "Post saved to favorites by " + userDetails.getUsername(),
                DtoMapper.toPostResponse(post)));
    }

    @GetMapping("/favorites")
    @PreAuthorize("hasAnyRole('ADMIN', 'AUTHOR', 'READER')")
    @Operation(summary = "Get favorites", description = "Get all saved/favorite posts")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getFavorites(
            @AuthenticationPrincipal UserDetails userDetails) {
        // Derive favorites from user's commented posts (engagement-based)
        UserResponse user = userService.getByUsername(userDetails.getUsername());
        List<PostResponse> favorites = commentService.getByUser(user.getId()).stream()
                .map(Comment::getPost)
                .filter(p -> p != null)
                .distinct()
                .map(DtoMapper::toPostResponse)
                .collect(Collectors.toList());
        Map<String, Object> response = new HashMap<>();
        response.put("username", userDetails.getUsername());
        response.put("favorites", favorites);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/reading-history")
    @PreAuthorize("hasAnyRole('ADMIN', 'AUTHOR', 'READER')")
    @Operation(summary = "Get reading history", description = "Get user's reading history based on commented posts")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getReadingHistory(
            @AuthenticationPrincipal UserDetails userDetails) {
        UserResponse user = userService.getByUsername(userDetails.getUsername());
        List<PostResponse> history = commentService.getByUser(user.getId()).stream()
                .map(Comment::getPost)
                .filter(p -> p != null)
                .distinct()
                .map(DtoMapper::toPostResponse)
                .collect(Collectors.toList());
        Map<String, Object> response = new HashMap<>();
        response.put("username", userDetails.getUsername());
        response.put("history", history);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
