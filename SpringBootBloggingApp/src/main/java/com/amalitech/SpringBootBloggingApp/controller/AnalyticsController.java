package com.amalitech.SpringBootBloggingApp.controller;

import com.amalitech.SpringBootBloggingApp.model.dto.response.ApiResponse;
import com.amalitech.SpringBootBloggingApp.model.dto.response.PostViewStats;
import com.amalitech.SpringBootBloggingApp.model.entity.Post;
import com.amalitech.SpringBootBloggingApp.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1/analytics")
@Tag(name = "Analytics", description = "Post view counts, trending posts, and feed aggregation")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    /**
     * Record a view for a post.
     * Fire-and-forget: returns immediately while the increment runs on the async thread pool.
     */
    @PostMapping("/posts/{postId}/view")
    @Operation(summary = "Record a view for a post")
    public CompletableFuture<ResponseEntity<ApiResponse<Void>>> recordView(
            @PathVariable String postId) {
        return analyticsService.recordViewAsync(postId)
                .thenApply(v -> ResponseEntity.ok(
                        ApiResponse.success("View recorded", null)));
    }

    /**
     * Get the current view count for a specific post.
     */
    @GetMapping("/posts/{postId}/views")
    @Operation(summary = "Get view count for a post")
    public CompletableFuture<ResponseEntity<ApiResponse<Long>>> getViewCount(
            @PathVariable String postId) {
        return analyticsService.getViewCountAsync(postId)
                .thenApply(count -> ResponseEntity.ok(
                        ApiResponse.success("View count retrieved", count)));
    }

    /**
     * Get the top-N trending posts ranked by view count (descending).
     * Public endpoint – no authentication required.
     */
    @GetMapping("/trending")
    @Operation(summary = "Get trending posts by view count")
    public CompletableFuture<ResponseEntity<ApiResponse<List<PostViewStats>>>> getTrending(
            @RequestParam(defaultValue = "10") int limit) {
        return analyticsService.getTrendingPostsAsync(limit)
                .thenApply(trending -> ResponseEntity.ok(
                        ApiResponse.success("Trending posts retrieved", trending)));
    }

    /**
     * Get a personalised feed for the authenticated user.
     * Published posts sorted by view count then by creation date.
     */
    @GetMapping("/feed")
    @Operation(summary = "Get personalised feed for authenticated user")
    public CompletableFuture<ResponseEntity<ApiResponse<List<Post>>>> getFeed(
            Authentication authentication) {
        String userId = authentication.getName();
        return analyticsService.aggregateFeedAsync(userId)
                .thenApply(feed -> ResponseEntity.ok(
                        ApiResponse.success("Feed retrieved", feed)));
    }

    /**
     * Raw snapshot of all in-memory view counts (postId → count).
     * Useful for debugging and profiling.
     */
    @GetMapping("/views/snapshot")
    @Operation(summary = "Get full view-count snapshot (all tracked posts)")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getSnapshot() {
        return ResponseEntity.ok(
                ApiResponse.success("Snapshot retrieved",
                        analyticsService.getViewCountSnapshot()));
    }
}
