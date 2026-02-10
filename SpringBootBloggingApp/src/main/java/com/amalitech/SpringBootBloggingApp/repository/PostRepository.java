package com.amalitech.SpringBootBloggingApp.repository;

import com.amalitech.SpringBootBloggingApp.model.entity.Post;
import com.amalitech.SpringBootBloggingApp.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends MongoRepository<Post, String> {
    
    // Derived query methods - automatically implemented by Spring Data
    List<Post> findByAuthor(User author);
    
    List<Post> findByAuthorId(String authorId);
    
    List<Post> findByPublished(boolean published);
    
    List<Post> findByPublishedTrue();
    
    List<Post> findByTitleContaining(String keyword);
    
    List<Post> findByCreatedAtBetween(Long startDate, Long endDate);
    
    // Pagination support for published posts
    Page<Post> findByPublished(boolean published, Pageable pageable);
    
    Page<Post> findByAuthor(User author, Pageable pageable);
    
    Page<Post> findByAuthorId(String authorId, Pageable pageable);
    
    // Custom query with regex for flexible title search
    @Query("{ 'title': { $regex: ?0, $options: 'i' } }")
    List<Post> searchByTitle(String keyword);
    
    @Query("{ 'title': { $regex: ?0, $options: 'i' } }")
    Page<Post> searchByTitle(String keyword, Pageable pageable);
    
    // Custom query to find posts by tag name
    @Query(value = "{ '_id': { $in: ?0 } }")
    List<Post> findByPostIds(List<String> postIds);
    
    @Query(value = "{ '_id': { $in: ?0 } }")
    Page<Post> findByPostIds(List<String> postIds, Pageable pageable);
    
    // Count posts by author
    long countByAuthor(User author);
    
    long countByPublished(boolean published);
}
