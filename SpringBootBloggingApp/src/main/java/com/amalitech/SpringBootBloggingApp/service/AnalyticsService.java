package com.amalitech.SpringBootBloggingApp.service;

import com.amalitech.SpringBootBloggingApp.model.dto.response.PostViewStats;
import com.amalitech.SpringBootBloggingApp.model.entity.Post;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Async analytics operations for the blogging platform.
 * All methods return {@link CompletableFuture} so callers are never blocked.
 */
public interface AnalyticsService {

    /** Increment the view counter for a post (fire-and-forget). */
    CompletableFuture<Void> recordViewAsync(String postId);

    /** Retrieve the current view count for a post. */
    CompletableFuture<Long> getViewCountAsync(String postId);

    /**
     * Return the top {@code limit} posts ranked by view count, descending.
     * Uses a parallel stream internally for fast in-memory sorting.
     */
    CompletableFuture<List<PostViewStats>> getTrendingPostsAsync(int limit);

    /**
     * Aggregate a personalised feed for a user: all published posts,
     * prioritised by view count (most-viewed first).
     */
    CompletableFuture<List<Post>> aggregateFeedAsync(String userId);

    /** Return a snapshot of all tracked view counts (postId → count). */
    java.util.Map<String, Long> getViewCountSnapshot();
}
