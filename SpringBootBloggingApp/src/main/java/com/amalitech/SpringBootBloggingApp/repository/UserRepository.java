package com.amalitech.SpringBootBloggingApp.repository;

import com.amalitech.SpringBootBloggingApp.model.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    
    // Derived query methods - Spring Data automatically implements these
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    // Custom query for login verification
    @Query("{ 'email': ?0 }")
    Optional<User> findByEmailForLogin(String email);
}
