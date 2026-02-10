package com.amalitech.SpringBootBloggingApp.repository;

import com.amalitech.SpringBootBloggingApp.model.entity.PostTag;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostTagRepository extends MongoRepository<PostTag, String> {
    
    // Derived query methods
    List<PostTag> findByPostId(String postId);
    
    List<PostTag> findByTagId(String tagId);
    
    Optional<PostTag> findByPostIdAndTagId(String postId, String tagId);
    
    boolean existsByPostIdAndTagId(String postId, String tagId);
    
    // Delete operations
    void deleteByPostId(String postId);
    
    void deleteByTagId(String tagId);
    
    void deleteByPostIdAndTagId(String postId, String tagId);
    
    // Count operations for statistics
    long countByPostId(String postId);
    
    long countByTagId(String tagId);
}
