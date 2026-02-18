package com.amalitech.SpringBootBloggingApp.repository;

import com.amalitech.SpringBootBloggingApp.model.entity.Post;
import com.amalitech.SpringBootBloggingApp.model.entity.Review;
import com.amalitech.SpringBootBloggingApp.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends MongoRepository<Review, String> {
    
    // Derived query methods
    List<Review> findByPostId(String postId);
    
    List<Review> findByPost(Post post);
    
    List<Review> findByUserId(String userId);
    
    List<Review> findByUser(User user);
    
    List<Review> findByRating(int rating);
    
    List<Review> findByRatingGreaterThanEqual(int rating);
    
    // Pagination support
    Page<Review> findByPost(Post post, Pageable pageable);
    
    Page<Review> findByPostId(String postId, Pageable pageable);
    
    Page<Review> findByUserId(String userId, Pageable pageable);
    
    // Count methods
    long countByPost(Post post);
    
    long countByPostId(String postId);
    
    // Check if user already reviewed a post
    boolean existsByPostIdAndUserId(String postId, String userId);
    
    // MongoDB Aggregation for average rating
    @Aggregation(pipeline = {
        "{ $match: { 'post.$id': { $eq: ?0 } } }",
        "{ $group: { _id: null, avgRating: { $avg: '$rating' } } }"
    })
    Double calculateAverageRatingByPostId(String postId);
    
    // Find recent reviews
    List<Review> findTop10ByOrderByCreatedAtDesc();
}
