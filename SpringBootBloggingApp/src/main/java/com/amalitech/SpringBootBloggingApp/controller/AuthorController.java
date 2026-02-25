package com.amalitech.SpringBootBloggingApp.controller;

import com.amalitech.SpringBootBloggingApp.model.dto.DtoMapper;
import com.amalitech.SpringBootBloggingApp.model.dto.request.CreatePostRequest;
import com.amalitech.SpringBootBloggingApp.model.dto.request.UpdatePostRequest;
import com.amalitech.SpringBootBloggingApp.model.dto.response.ApiResponse;
import com.amalitech.SpringBootBloggingApp.model.dto.response.PostResponse;
import com.amalitech.SpringBootBloggingApp.model.dto.response.UserResponse;
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

@RestController
@RequestMapping("/api/author")
@Tag(name = "Author", description = "Author operations for content creation and management")
@SecurityRequirement(name = "Bearer Authentication")
public class AuthorController {

    private final PostService postService;
    private final UserService userService;
    private final CommentService commentService;

    public AuthorController(PostService postService, UserService userService, CommentService commentService) {
        this.postService = postService;
        this.userService = userService;
        this.commentService = commentService;
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('ADMIN', 'AUTHOR')")
    @Operation(summary = "Author dashboard", description = "Get author dashboard with statistics and content overview")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAuthorDashboard(
            @AuthenticationPrincipal UserDetails userDetails) {
        UserResponse user = userService.getByUsername(userDetails.getUsername());
        List<Post> posts = postService.getByAuthor(user.getId());
        long totalComments = posts.stream()
                .mapToLong(p -> commentService.getByPost(p.getId()).size())
                .sum();
        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("username", userDetails.getUsername());
        dashboard.put("message", "Welcome to Author Dashboard");
        dashboard.put("totalPosts", posts.size());
        dashboard.put("publishedPosts", posts.stream().filter(Post::isPublished).count());
        dashboard.put("draftPosts", posts.stream().filter(p -> !p.isPublished()).count());
        dashboard.put("totalComments", totalComments);
        return ResponseEntity.ok(ApiResponse.success(dashboard));
    }

    @PostMapping("/posts")
    @PreAuthorize("hasAnyRole('ADMIN', 'AUTHOR')")
    @Operation(summary = "Create post", description = "Create a new blog post")
    public ResponseEntity<ApiResponse<PostResponse>> createPost(
            @Valid @RequestBody CreatePostRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        UserResponse user = userService.getByUsername(userDetails.getUsername());
        request.setAuthorId(user.getId());
        Post created = postService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Post created successfully", DtoMapper.toPostResponse(created)));
    }

    @PutMapping("/posts/{postId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AUTHOR')")
    @Operation(summary = "Update post", description = "Update an existing blog post")
    public ResponseEntity<ApiResponse<PostResponse>> updatePost(
            @PathVariable String postId,
            @Valid @RequestBody UpdatePostRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        Post existing = postService.getById(postId);
        if (existing == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Post not found"));
        }
        UserResponse user = userService.getByUsername(userDetails.getUsername());
        // Allow update only if the requester owns the post or is an ADMIN
        boolean isOwner = existing.getAuthor() != null && existing.getAuthor().getId().equals(user.getId());
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isOwner && !isAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error("You are not allowed to update this post"));
        }
        existing.setTitle(request.getTitle());
        existing.setContent(request.getContent());
        existing.setPublished(request.isPublished());
        existing.setUpdatedAt(System.currentTimeMillis());
        Post updated = postService.update(existing);
        return ResponseEntity.ok(ApiResponse.success("Post updated successfully", DtoMapper.toPostResponse(updated)));
    }

    @DeleteMapping("/posts/{postId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete post", description = "Delete a blog post (Admin only)")
    public ResponseEntity<ApiResponse<Void>> deletePost(
            @PathVariable String postId,
            @AuthenticationPrincipal UserDetails userDetails) {
        Post existing = postService.getById(postId);
        if (existing == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Post not found"));
        }
        postService.delete(postId);
        return ResponseEntity.ok(ApiResponse.success("Post deleted successfully", null));
    }

    @GetMapping("/analytics")
    @PreAuthorize("hasAnyRole('ADMIN', 'AUTHOR')")
    @Operation(summary = "View analytics", description = "Get author content analytics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAnalytics(
            @AuthenticationPrincipal UserDetails userDetails) {
        UserResponse user = userService.getByUsername(userDetails.getUsername());
        List<Post> posts = postService.getByAuthor(user.getId());
        long totalComments = posts.stream()
                .mapToLong(p -> commentService.getByPost(p.getId()).size())
                .sum();
        PostResponse topPost = posts.stream()
                .max((a, b) -> Long.compare(
                        commentService.getByPost(a.getId()).size(),
                        commentService.getByPost(b.getId()).size()))
                .map(DtoMapper::toPostResponse)
                .orElse(null);
        Map<String, Object> analytics = new HashMap<>();
        analytics.put("username", userDetails.getUsername());
        analytics.put("totalPosts", posts.size());
        analytics.put("publishedPosts", posts.stream().filter(Post::isPublished).count());
        analytics.put("draftPosts", posts.stream().filter(p -> !p.isPublished()).count());
        analytics.put("totalComments", totalComments);
        analytics.put("topPost", topPost);
        return ResponseEntity.ok(ApiResponse.success(analytics));
    }
}
