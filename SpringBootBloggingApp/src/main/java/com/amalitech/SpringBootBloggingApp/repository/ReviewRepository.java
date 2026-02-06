package com.amalitech.SpringBootBloggingApp.repository;

import com.amalitech.SpringBootBloggingApp.model.entity.Review;

import java.util.List;

public interface ReviewRepository extends BaseRepository<Review> {

    List<Review> findByPostId(String postId);
    List<Review> findByPost(com.amalitech.SpringBootBloggingApp.model.entity.Post post);
    List<Review> findByUserId(String userId);
    List<Review> findByUser(com.amalitech.SpringBootBloggingApp.model.entity.User user);
    
    double calculateAverageRating(String postId);
    double calculateAverageRating(com.amalitech.SpringBootBloggingApp.model.entity.Post post);
}
