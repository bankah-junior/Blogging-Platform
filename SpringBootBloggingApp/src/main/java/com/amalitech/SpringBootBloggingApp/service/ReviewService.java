package com.amalitech.SpringBootBloggingApp.service;

import com.amalitech.SpringBootBloggingApp.model.dto.request.CreateReviewRequest;
import com.amalitech.SpringBootBloggingApp.model.dto.response.PageResponse;
import com.amalitech.SpringBootBloggingApp.model.entity.Review;

import java.util.List;

public interface ReviewService {

    Review create(Review review);

    Review create(CreateReviewRequest request);

    Review getById(String reviewId);

    boolean delete(String reviewId);

    boolean update(Review review);

    List<Review> getByPost(String postId);
    
    PageResponse<Review> getByPostPaginated(String postId, int page, int size);
    
    List<Review> getByPost(com.amalitech.SpringBootBloggingApp.model.entity.Post post);
    
    List<Review> getByUser(String userId);
    
    PageResponse<Review> getByUserPaginated(String userId, int page, int size);
    
    List<Review> getByUser(com.amalitech.SpringBootBloggingApp.model.entity.User user);

    double getAverageRatingForPost(String postId);
    double getAverageRatingForPost(com.amalitech.SpringBootBloggingApp.model.entity.Post post);

    List<Review> getAll();

    PageResponse<Review> getAllPaginated(int page, int size);
}
