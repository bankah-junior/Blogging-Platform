package com.amalitech.bloggingplatform.service.impl;

import com.amalitech.bloggingplatform.dao.ReviewDAO;
import com.amalitech.bloggingplatform.dao.impl.ReviewDAOImpl;
import com.amalitech.bloggingplatform.model.Review;
import com.amalitech.bloggingplatform.service.ReviewService;
import com.amalitech.bloggingplatform.utils.MongoDBConnection;

import java.util.List;

public class ReviewServiceImpl implements ReviewService {
    private final ReviewDAO reviewDAO;

    public ReviewServiceImpl() {
        this.reviewDAO = new ReviewDAOImpl(MongoDBConnection.connect().getDatabase("java-demo"));
    }

    @Override
    public Review create(Review review) {
        return reviewDAO.save(review);
    }

    @Override
    public boolean delete(String reviewId) {
        return reviewDAO.deleteById(reviewId);
    }

    @Override
    public List<Review> getByPost(String postId) {
        return reviewDAO.findByPostId(postId);
    }

    @Override
    public double getAverageRatingForPost(String postId) {
        return reviewDAO.calculateAverageRating(postId);
    }
}
