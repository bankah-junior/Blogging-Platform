package com.amalitech.SpringBootBloggingApp.service.impl;

import com.amalitech.SpringBootBloggingApp.model.dto.response.PostViewStats;
import com.amalitech.SpringBootBloggingApp.model.entity.Post;
import com.amalitech.SpringBootBloggingApp.repository.PostRepository;
import com.amalitech.SpringBootBloggingApp.service.AnalyticsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Async analytics service.
 *
 * View counts are stored in a {@link ConcurrentHashMap}&lt;postId, {@link AtomicLong}&gt;
 * for lock-free, thread-safe increments across many concurrent requests.
 *
 * Every method is annotated with {@code @Async("taskExecutor")} so it runs on the
 * dedicated thread pool defined in {@code AsyncConfig}, keeping the servlet threads
 * free for new incoming HTTP requests.
 */
@Service
public class AnalyticsServiceImpl implements AnalyticsService {

    private static final Logger log = LoggerFactory.getLogger(AnalyticsServiceImpl.class);

    /**
     * Thread-safe, lock-free view-count store.
     * Key  = postId
     * Value = AtomicLong view counter
     */
    private final ConcurrentHashMap<String, AtomicLong> viewCounts = new ConcurrentHashMap<>();

    private final PostRepository postRepository;

    public AnalyticsServiceImpl(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    // ------------------------------------------------------------------ //
    //  Async operations                                                    //
    // ------------------------------------------------------------------ //

    @Override
    @Async("taskExecutor")
    public CompletableFuture<Void> recordViewAsync(String postId) {
        // computeIfAbsent is atomic; incrementAndGet on AtomicLong is also atomic
        long newCount = viewCounts
                .computeIfAbsent(postId, id -> new AtomicLong(0))
                .incrementAndGet();
        log.debug("View recorded for post={} totalViews={}", postId, newCount);
        return CompletableFuture.completedFuture(null);
    }

    @Override
    @Async("taskExecutor")
    public CompletableFuture<Long> getViewCountAsync(String postId) {
        AtomicLong counter = viewCounts.get(postId);
        long count = counter != null ? counter.get() : 0L;
        return CompletableFuture.completedFuture(count);
    }

    /**
     * Ranks every tracked post by view count using a parallel stream.
     * Parallel processing is safe here because viewCounts is a read-only
     * snapshot during this computation.
     */
    @Override
    @Async("taskExecutor")
    public CompletableFuture<List<PostViewStats>> getTrendingPostsAsync(int limit) {
        // Take an immutable snapshot to avoid ConcurrentModificationException
        Map<String, Long> snapshot = getViewCountSnapshot();

        if (snapshot.isEmpty()) {
            return CompletableFuture.completedFuture(List.of());
        }

        // Fetch only the posts that have been viewed (avoids a full collection scan)
        List<String> trackedIds = List.copyOf(snapshot.keySet());
        List<Post> posts = postRepository.findByPostIds(trackedIds);

        // Parallel stream: safe because snapshot is immutable and posts list is local
        List<PostViewStats> trending = posts.parallelStream()
                .map(post -> new PostViewStats(
                        post.getId(),
                        post.getTitle(),
                        snapshot.getOrDefault(post.getId(), 0L)))
                .sorted(Comparator.comparingLong(PostViewStats::getViewCount).reversed())
                .limit(limit)
                .collect(Collectors.toList());

        log.info("getTrendingPostsAsync: returning {} trending posts (limit={})",
                trending.size(), limit);
        return CompletableFuture.completedFuture(trending);
    }

    /**
     * Aggregates a personalised feed for a user:
     * all published posts sorted by view count (most-viewed first),
     * then by creation date for posts with equal view counts.
     */
    @Override
    @Async("taskExecutor")
    public CompletableFuture<List<Post>> aggregateFeedAsync(String userId) {
        log.info("aggregateFeedAsync: building feed for userId={}", userId);

        Map<String, Long> snapshot = getViewCountSnapshot();
        List<Post> published = postRepository.findByPublishedTrue();

        // Parallel stream for large feeds; safe as snapshot & list are local
        List<Post> feed = published.parallelStream()
                .sorted(Comparator
                        .comparingLong((Post p) ->
                                snapshot.getOrDefault(p.getId(), 0L))
                        .reversed()
                        .thenComparingLong(p ->
                                p.getCreatedAt() != null ? -p.getCreatedAt() : 0L))
                .collect(Collectors.toList());

        log.info("aggregateFeedAsync: feed size={} for userId={}", feed.size(), userId);
        return CompletableFuture.completedFuture(feed);
    }

    // ------------------------------------------------------------------ //
    //  Snapshot & maintenance                                              //
    // ------------------------------------------------------------------ //

    /** Returns an immutable copy of current view counts for safe parallel processing. */
    @Override
    public Map<String, Long> getViewCountSnapshot() {
        return viewCounts.entrySet().stream()
                .collect(Collectors.toUnmodifiableMap(
                        Map.Entry::getKey,
                        e -> e.getValue().get()));
    }

    /**
     * Periodically removes entries for posts that no longer exist in the database,
     * preventing unbounded memory growth. Runs once per hour.
     */
    @Scheduled(fixedRateString = "${app.analytics.cleanup-rate-ms:3600000}")
    public void cleanupStaleViewCounts() {
        if (viewCounts.isEmpty()) return;

        List<String> trackedIds = List.copyOf(viewCounts.keySet());
        List<Post> existing = postRepository.findByPostIds(trackedIds);
        var existingIds = existing.stream()
                .map(Post::getId)
                .collect(Collectors.toSet());

        int removedCount = 0;
        for (String id : trackedIds) {
            if (!existingIds.contains(id)) {
                viewCounts.remove(id);
                removedCount++;
            }
        }
        if (removedCount > 0) {
            log.info("cleanupStaleViewCounts: removed {} stale entries, remaining={}",
                    removedCount, viewCounts.size());
        }
    }
}
