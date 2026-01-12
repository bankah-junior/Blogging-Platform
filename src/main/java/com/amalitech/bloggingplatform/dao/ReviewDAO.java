package com.amalitech.bloggingplatform.dao;

import com.amalitech.bloggingplatform.model.Review;

import java.util.List;

public interface ReviewDAO extends BaseDAO<Review> {

    List<Review> findByPostId(String postId);

    double calculateAverageRating(String postId);
}
