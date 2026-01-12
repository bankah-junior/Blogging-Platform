package com.amalitech.bloggingplatform.dao.impl;

import com.amalitech.bloggingplatform.dao.ReviewDAO;
import com.amalitech.bloggingplatform.model.Review;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.mongodb.client.model.Filters.eq;

public class ReviewDAOImpl implements ReviewDAO {

    private final MongoCollection<Document> reviewsCollection;

    public ReviewDAOImpl(MongoDatabase database) {
        if (database == null) {
            throw new IllegalArgumentException("database must not be null");
        }
        this.reviewsCollection = database.getCollection("reviews", Document.class);

        reviewsCollection.createIndex(new Document("postId", 1));

        reviewsCollection.createIndex(
                new Document("postId", 1).append("userId", 1),
                new IndexOptions().unique(true));
    }

    @Override
    public List<Review> findByPostId(String postId) {
        List<Review> reviews = new ArrayList<>();

        reviewsCollection.find(eq("postId", postId))
                .forEach(doc -> reviews.add(mapToReview(doc)));

        return reviews;
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
    public Review save(Review entity) {

        Document doc = new Document("postId", entity.getPostId())
                .append("userId", entity.getUserId())
                .append("rating", entity.getRating())
                .append("feedback", entity.getFeedback())
                .append("createdAt", System.currentTimeMillis());

        reviewsCollection.insertOne(doc);

        ObjectId id = doc.getObjectId("_id");
        if (id != null) {
            entity.setId(id.toHexString());
            return entity;
        }
        return null;
    }

    @Override
    public Optional<Review> findById(String id) {
        Document doc = reviewsCollection
                .find(eq("_id", new ObjectId(id)))
                .first();

        return doc != null ? Optional.of(mapToReview(doc)) : Optional.empty();
    }

    @Override
    public List<Review> findAll() {
        List<Review> reviews = new ArrayList<>();

        reviewsCollection.find()
                .forEach(doc -> reviews.add(mapToReview(doc)));

        return reviews;
    }

    @Override
    public boolean update(Review entity) {

        Document query = new Document("_id", new ObjectId(entity.getId()));
        Document update = new Document("$set",
                new Document("rating", entity.getRating())
                        .append("feedback", entity.getFeedback())
                        .append("updatedAt", System.currentTimeMillis())
        );

        return reviewsCollection.updateOne(query, update).getModifiedCount() > 0;
    }

    @Override
    public boolean deleteById(String id) {
        return reviewsCollection
                .deleteOne(eq("_id", new ObjectId(id)))
                .getDeletedCount() > 0;
    }

    private Review mapToReview(Document doc) {
        return new Review(
                doc.getObjectId("_id").toHexString(),
                doc.getString("postId"),
                doc.getString("userId"),
                doc.getInteger("rating"),
                doc.getString("feedback"),
                doc.getLong("createdAt"),
                doc.getLong("updatedAt")
        );
    }
}
