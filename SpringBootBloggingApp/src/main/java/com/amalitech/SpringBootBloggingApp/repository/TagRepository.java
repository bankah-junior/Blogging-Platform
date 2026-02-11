package com.amalitech.SpringBootBloggingApp.repository;

import com.amalitech.SpringBootBloggingApp.model.entity.Tag;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends MongoRepository<Tag, String> {
    
    // Derived query methods
    Optional<Tag> findByName(String name);
    
    boolean existsByName(String name);
    
    List<Tag> findByNameContaining(String keyword);
    
    // Custom query to find tags by partial name match (case-insensitive)
    @Query("{ 'name': { $regex: ?0, $options: 'i' } }")
    List<Tag> searchByName(String keyword);
    
    // Find tags ordered by name
    List<Tag> findAllByOrderByNameAsc();
}
