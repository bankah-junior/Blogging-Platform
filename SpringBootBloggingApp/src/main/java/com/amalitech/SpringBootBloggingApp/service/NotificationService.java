package com.amalitech.SpringBootBloggingApp.service;

import java.util.concurrent.CompletableFuture;

/**
 * Async notification dispatcher for blog events.
 *
 * All methods return {@link CompletableFuture}{@code <Void>} and are annotated
 * with {@code @Async} in the implementation, making every call fire-and-forget —
 * the calling thread is never blocked waiting for the notification to be sent.
 */
public interface NotificationService {

    /**
     * Notify the post author that a new comment was left on their post.
     *
     * @param postId           the ID of the post that was commented on
     * @param postTitle        title of the post
     * @param commenterUsername username of the user who commented
     * @param authorEmail      e-mail address of the post author (recipient)
     */
    CompletableFuture<Void> notifyNewComment(String postId, String postTitle,
                                              String commenterUsername, String authorEmail);

    /**
     * Notify subscribers / the author that a post has been published.
     *
     * @param postId       the ID of the newly published post
     * @param postTitle    title of the post
     * @param authorUsername username of the post author
     */
    CompletableFuture<Void> notifyPostPublished(String postId, String postTitle,
                                                 String authorUsername);

    /**
     * Notify the post author that a new review was submitted for their post.
     *
     * @param postId            the ID of the reviewed post
     * @param postTitle         title of the post
     * @param reviewerUsername  username of the reviewer
     * @param rating            the numeric rating given
     */
    CompletableFuture<Void> notifyNewReview(String postId, String postTitle,
                                             String reviewerUsername, int rating);
}
