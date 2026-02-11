package com.amalitech.SpringBootBloggingApp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/reader")
@Tag(name = "Reader", description = "Reader operations for consuming and interacting with content")
@SecurityRequirement(name = "Bearer Authentication")
public class ReaderController {

    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('ADMIN', 'AUTHOR', 'READER')")
    @Operation(summary = "Reader dashboard", description = "Get reader dashboard with personalized content")
    public ResponseEntity<Map<String, Object>> getReaderDashboard(
            @AuthenticationPrincipal UserDetails userDetails) {
        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("username", userDetails.getUsername());
        dashboard.put("message", "Welcome to Reader Dashboard");
        dashboard.put("recommendedPosts", new ArrayList<>());
        dashboard.put("recentlyRead", new ArrayList<>());
        dashboard.put("savedPosts", new ArrayList<>());
        return ResponseEntity.ok(dashboard);
    }

    @PostMapping("/posts/{postId}/like")
    @PreAuthorize("hasAnyRole('ADMIN', 'AUTHOR', 'READER')")
    @Operation(summary = "Like post", description = "Like a blog post")
    public ResponseEntity<Map<String, String>> likePost(
            @PathVariable String postId,
            @AuthenticationPrincipal UserDetails userDetails) {
        // Placeholder - integrate with LikeService
        return ResponseEntity.ok(Map.of(
                "message", "Post " + postId + " liked successfully",
                "likedBy", userDetails.getUsername()
        ));
    }

    @PostMapping("/posts/{postId}/comment")
    @PreAuthorize("hasAnyRole('ADMIN', 'AUTHOR', 'READER')")
    @Operation(summary = "Comment on post", description = "Add a comment to a blog post")
    public ResponseEntity<Map<String, String>> commentOnPost(
            @PathVariable String postId,
            @RequestBody Map<String, String> commentData,
            @AuthenticationPrincipal UserDetails userDetails) {
        // Placeholder - integrate with CommentService
        String content = commentData.get("content");
        return ResponseEntity.ok(Map.of(
                "message", "Comment added successfully",
                "postId", postId,
                "commentedBy", userDetails.getUsername()
        ));
    }

    @PostMapping("/posts/{postId}/save")
    @PreAuthorize("hasAnyRole('ADMIN', 'AUTHOR', 'READER')")
    @Operation(summary = "Save post", description = "Save a post to favorites")
    public ResponseEntity<Map<String, String>> savePost(
            @PathVariable String postId,
            @AuthenticationPrincipal UserDetails userDetails) {
        // Placeholder - integrate with FavoriteService
        return ResponseEntity.ok(Map.of(
                "message", "Post " + postId + " saved to favorites",
                "savedBy", userDetails.getUsername()
        ));
    }

    @GetMapping("/favorites")
    @PreAuthorize("hasAnyRole('ADMIN', 'AUTHOR', 'READER')")
    @Operation(summary = "Get favorites", description = "Get all saved/favorite posts")
    public ResponseEntity<Map<String, Object>> getFavorites(
            @AuthenticationPrincipal UserDetails userDetails) {
        Map<String, Object> response = new HashMap<>();
        response.put("username", userDetails.getUsername());
        response.put("favorites", new ArrayList<>());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/reading-history")
    @PreAuthorize("hasAnyRole('ADMIN', 'AUTHOR', 'READER')")
    @Operation(summary = "Get reading history", description = "Get user's reading history")
    public ResponseEntity<Map<String, Object>> getReadingHistory(
            @AuthenticationPrincipal UserDetails userDetails) {
        Map<String, Object> response = new HashMap<>();
        response.put("username", userDetails.getUsername());
        response.put("history", new ArrayList<>());
        return ResponseEntity.ok(response);
    }
}
