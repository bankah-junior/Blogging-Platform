package com.amalitech.bloggingplatform.controller;

import com.amalitech.bloggingplatform.model.Review;
import com.amalitech.bloggingplatform.service.ReviewService;
import com.amalitech.bloggingplatform.service.impl.ReviewServiceImpl;

public class ReviewController {
    private final ReviewService reviewService;

    public ReviewController() {
        this.reviewService = new ReviewServiceImpl();
    }

    public Review createReview(Review review) {
        return reviewService.create(review);
    }
}
