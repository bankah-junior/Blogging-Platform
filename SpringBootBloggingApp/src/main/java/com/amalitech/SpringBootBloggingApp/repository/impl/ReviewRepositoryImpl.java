package com.amalitech.SpringBootBloggingApp.repository.impl;

import com.amalitech.SpringBootBloggingApp.model.entity.Post;
import com.amalitech.SpringBootBloggingApp.model.entity.Review;
import com.amalitech.SpringBootBloggingApp.model.entity.User;
import com.amalitech.SpringBootBloggingApp.repository.ReviewRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ReviewRepositoryImpl implements ReviewRepository {
    private final MongoTemplate mongoTemplate;

    public ReviewRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Review save(Review entity) {
        return mongoTemplate.save(entity, "reviews");
    }

    @Override
    public Optional<Review> findById(String id) {
        var review = mongoTemplate.findById(id, Review.class, "reviews");
        return Optional.ofNullable(review);
    }

    @Override
    public List<Review> findAll() {
        return mongoTemplate.findAll(Review.class, "reviews");
    }

    @Override
    public List<Review> findAll(int skip, int limit) {
        var query = new Query().skip((long) skip).limit(limit);
        return mongoTemplate.find(query, Review.class, "reviews");
    }

    @Override
    public long count() {
        return mongoTemplate.count(new Query(), Review.class, "reviews");
    }

    @Override
    public boolean update(Review entity) {
        var query = new Query(Criteria.where("id").is(entity.getId()));
        var update = new org.springframework.data.mongodb.core.query.Update()
                .set("rating", entity.getRating())
                .set("feedback", entity.getFeedback());
        return mongoTemplate.updateFirst(query, update, Review.class, "reviews").wasAcknowledged();
    }

    @Override
    public boolean deleteById(String id) {
        var query = new Query(Criteria.where("id").is(id));
        return mongoTemplate.remove(query, Review.class, "reviews").getDeletedCount() > 0;
    }

    @Override
    public List<Review> findByPostId(String postId) {
        var query = new Query(Criteria.where("postId").is(postId));
        return mongoTemplate.find(query, Review.class, "reviews");
    }

    @Override
    public List<Review> findByPost(Post post) {
        return List.of();
    }

    @Override
    public List<Review> findByUserId(String userId) {
        return List.of();
    }

    @Override
    public List<Review> findByUser(User user) {
        return List.of();
    }

    @Override
    public double calculateAverageRating(String postId) {
        List<Review> reviews = findByPostId(postId);
        if (reviews.isEmpty()) {
            return 0.0;
        }

        int total = reviews.stream()
                .mapToInt(Review::getRating)
                .sum();

        return (double) total / reviews.size();
    }

    @Override
    public double calculateAverageRating(Post post) {
        return 0;
    }
}
