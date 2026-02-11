package com.amalitech.SpringBootBloggingApp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/author")
@Tag(name = "Author", description = "Author operations for content creation and management")
@SecurityRequirement(name = "Bearer Authentication")
public class AuthorController {

    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('ADMIN', 'AUTHOR')")
    @Operation(summary = "Author dashboard", description = "Get author dashboard with statistics and content overview")
    public ResponseEntity<Map<String, Object>> getAuthorDashboard(
            @AuthenticationPrincipal UserDetails userDetails) {
        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("username", userDetails.getUsername());
        dashboard.put("message", "Welcome to Author Dashboard");
        dashboard.put("totalPosts", 0); // Placeholder - implement with actual data
        dashboard.put("totalViews", 0);
        dashboard.put("totalComments", 0);
        return ResponseEntity.ok(dashboard);
    }

    @PostMapping("/posts")
    @PreAuthorize("hasAnyRole('ADMIN', 'AUTHOR')")
    @Operation(summary = "Create post", description = "Create a new blog post")
    public ResponseEntity<Map<String, String>> createPost(
            @RequestBody Map<String, Object> postData,
            @AuthenticationPrincipal UserDetails userDetails) {
        // Placeholder - integrate with PostService
        return ResponseEntity.ok(Map.of(
                "message", "Post created successfully by " + userDetails.getUsername(),
                "status", "draft"
        ));
    }

    @PutMapping("/posts/{postId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AUTHOR')")
    @Operation(summary = "Update post", description = "Update an existing blog post")
    public ResponseEntity<Map<String, String>> updatePost(
            @PathVariable String postId,
            @RequestBody Map<String, Object> postData,
            @AuthenticationPrincipal UserDetails userDetails) {
        // Placeholder - integrate with PostService
        return ResponseEntity.ok(Map.of(
                "message", "Post " + postId + " updated successfully",
                "updatedBy", userDetails.getUsername()
        ));
    }

    @DeleteMapping("/posts/{postId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete post", description = "Delete a blog post (Admin only)")
    public ResponseEntity<Map<String, String>> deletePost(
            @PathVariable String postId,
            @AuthenticationPrincipal UserDetails userDetails) {
        // Placeholder - integrate with PostService
        return ResponseEntity.ok(Map.of(
                "message", "Post " + postId + " deleted successfully",
                "deletedBy", userDetails.getUsername()
        ));
    }

    @GetMapping("/analytics")
    @PreAuthorize("hasAnyRole('ADMIN', 'AUTHOR')")
    @Operation(summary = "View analytics", description = "Get author content analytics")
    public ResponseEntity<Map<String, Object>> getAnalytics(
            @AuthenticationPrincipal UserDetails userDetails) {
        Map<String, Object> analytics = new HashMap<>();
        analytics.put("username", userDetails.getUsername());
        analytics.put("viewsLastMonth", 0);
        analytics.put("likesLastMonth", 0);
        analytics.put("commentsLastMonth", 0);
        analytics.put("topPost", null);
        return ResponseEntity.ok(analytics);
    }
}
