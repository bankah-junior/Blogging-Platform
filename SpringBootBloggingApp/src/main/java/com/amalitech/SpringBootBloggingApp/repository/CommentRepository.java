package com.amalitech.SpringBootBloggingApp.repository;

import com.amalitech.SpringBootBloggingApp.model.entity.Comment;
import com.amalitech.SpringBootBloggingApp.model.entity.Post;
import com.amalitech.SpringBootBloggingApp.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends MongoRepository<Comment, String> {
    
    // Derived query methods
    List<Comment> findByPostId(String postId);
    
    List<Comment> findByPost(Post post);
    
    List<Comment> findByUserId(String userId);
    
    List<Comment> findByUser(User user);
    
    // Pagination support
    Page<Comment> findByPostId(String postId, Pageable pageable);
    
    Page<Comment> findByPost(Post post, Pageable pageable);
    
    // Count methods for statistics
    long countByPost(Post post);
    
    long countByPostId(String postId);
    
    long countByUser(User user);
    
    // Find recent comments
    List<Comment> findTop10ByOrderByCreatedAtDesc();
    
    // Custom query to find comments in date range
    @Query("{ 'createdAt': { $gte: ?0, $lte: ?1 } }")
    List<Comment> findByCreatedAtBetween(Long startDate, Long endDate);
}
