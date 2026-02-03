package com.amalitech.bloggingplatform.service;

import com.amalitech.bloggingplatform.model.entity.Review;

import java.util.List;

public interface ReviewService {

    Review create(Review review);

    boolean delete(String reviewId);

    List<Review> getByPost(String postId);

    double getAverageRatingForPost(String postId);
}

