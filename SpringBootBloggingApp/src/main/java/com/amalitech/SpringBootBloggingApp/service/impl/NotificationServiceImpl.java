package com.amalitech.SpringBootBloggingApp.service.impl;

import com.amalitech.SpringBootBloggingApp.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.LongAdder;

/**
 * Async notification dispatcher.
 *
 * Each method runs on the "taskExecutor" thread pool (defined in AsyncConfig)
 * so the servlet / service thread that triggered the event is released immediately.
 *
 * In a production system these methods would delegate to an e-mail provider
 * (e.g. SendGrid, SES) or a push-notification broker (e.g. Firebase FCM).
 * Here they simulate the I/O with a short sleep and structured log output,
 * which keeps the code testable without external dependencies.
 *
 * Dispatch counters (per event type) are tracked via {@link LongAdder} for
 * thread-safe, lock-free metrics collection.
 */
@Service
public class NotificationServiceImpl implements NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);

    // Thread-safe counters – LongAdder is optimal for high-contention increment-only use
    private final LongAdder commentNotificationsSent  = new LongAdder();
    private final LongAdder publishNotificationsSent  = new LongAdder();
    private final LongAdder reviewNotificationsSent   = new LongAdder();

    // ------------------------------------------------------------------ //
    //  Async notification methods                                          //
    // ------------------------------------------------------------------ //

    @Override
    @Async("taskExecutor")
    public CompletableFuture<Void> notifyNewComment(String postId,
                                                     String postTitle,
                                                     String commenterUsername,
                                                     String authorEmail) {
        try {
            log.info("[NOTIFY] New comment on post '{}' (id={}) by '{}' → dispatching to {}",
                    postTitle, postId, commenterUsername, authorEmail);

            simulateDispatch();

            commentNotificationsSent.increment();
            log.info("[NOTIFY] Comment notification sent to {} (total dispatched: {})",
                    authorEmail, commentNotificationsSent.sum());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("[NOTIFY] Comment notification interrupted for postId={}", postId);
        }
        return CompletableFuture.completedFuture(null);
    }

    @Override
    @Async("taskExecutor")
    public CompletableFuture<Void> notifyPostPublished(String postId,
                                                        String postTitle,
                                                        String authorUsername) {
        try {
            log.info("[NOTIFY] Post published '{}' (id={}) by '{}' → dispatching to subscribers",
                    postTitle, postId, authorUsername);

            simulateDispatch();

            publishNotificationsSent.increment();
            log.info("[NOTIFY] Publish notification dispatched for post '{}' (total dispatched: {})",
                    postTitle, publishNotificationsSent.sum());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("[NOTIFY] Publish notification interrupted for postId={}", postId);
        }
        return CompletableFuture.completedFuture(null);
    }

    @Override
    @Async("taskExecutor")
    public CompletableFuture<Void> notifyNewReview(String postId,
                                                    String postTitle,
                                                    String reviewerUsername,
                                                    int rating) {
        try {
            log.info("[NOTIFY] New review ({}/5) on post '{}' (id={}) by '{}'",
                    rating, postTitle, postId, reviewerUsername);

            simulateDispatch();

            reviewNotificationsSent.increment();
            log.info("[NOTIFY] Review notification dispatched for post '{}' (total dispatched: {})",
                    postTitle, reviewNotificationsSent.sum());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("[NOTIFY] Review notification interrupted for postId={}", postId);
        }
        return CompletableFuture.completedFuture(null);
    }

    // ------------------------------------------------------------------ //
    //  Metrics accessors (used by MetricsService / tests)                  //
    // ------------------------------------------------------------------ //

    public long getCommentNotificationCount()  { return commentNotificationsSent.sum(); }
    public long getPublishNotificationCount()  { return publishNotificationsSent.sum(); }
    public long getReviewNotificationCount()   { return reviewNotificationsSent.sum(); }

    // ------------------------------------------------------------------ //
    //  Helpers                                                             //
    // ------------------------------------------------------------------ //

    /**
     * Simulates the latency of a real notification dispatch (e.g. HTTP call
     * to an e-mail API). Replace with actual provider SDK call in production.
     */
    private void simulateDispatch() throws InterruptedException {
        Thread.sleep(50); // 50 ms simulated I/O
    }
}
