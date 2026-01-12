package com.amalitech.bloggingplatform.service.impl;

import com.amalitech.bloggingplatform.model.Review;
import com.amalitech.bloggingplatform.service.ReviewService;

import java.util.List;

public class ReviewServiceImpl implements ReviewService {

    @Override
    public Review create(Review review) {
        return null;
    }

    @Override
    public boolean delete(String reviewId) {
        return false;
    }

    @Override
    public List<Review> getByPost(String postId) {
        return List.of();
    }

    @Override
    public double getAverageRatingForPost(String postId) {
        return 0;
    }
}
