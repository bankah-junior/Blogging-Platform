package com.amalitech.bloggingplatform.dao.impl;

import com.amalitech.bloggingplatform.dao.CommentDAO;
import com.amalitech.bloggingplatform.model.Comment;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.mongodb.client.model.Filters.eq;

public class CommentDAOImpl implements CommentDAO {

    private final MongoCollection<Document> commentsCollection;

    public CommentDAOImpl(MongoDatabase database) {
        if (database == null) {
            throw new IllegalArgumentException("database must not be null");
        }
        this.commentsCollection = database.getCollection("comments", Document.class);

        commentsCollection.createIndex(new Document("postId", 1));
    }

    @Override
    public List<Comment> findByPostId(String postId) {
        List<Comment> comments = new ArrayList<>();

        commentsCollection.find(eq("postId", postId))
                .forEach(doc -> comments.add(mapToComment(doc)));

        return comments;
    }

    @Override
    public Comment save(Comment entity) {

        Document doc = new Document("postId", entity.getPostId())
                .append("userId", entity.getUserId())
                .append("content", entity.getContent())
                .append("createdAt", System.currentTimeMillis())
                .append("updatedAt", null);

        commentsCollection.insertOne(doc);

        ObjectId id = doc.getObjectId("_id");
        if (id != null) {
            entity.setId(id.toHexString());
            return entity;
        }

        return null;
    }

    @Override
    public Optional<Comment> findById(String id) {
        Document query = new Document("_id", new ObjectId(id));
        Document doc = commentsCollection.find(query).first();

        if (doc != null) {
            return Optional.of(mapToComment(doc));
        }
        return Optional.empty();
    }

    @Override
    public List<Comment> findAll() {
        List<Comment> comments = new ArrayList<>();

        commentsCollection.find()
                .forEach(doc -> comments.add(mapToComment(doc)));

        return comments;
    }

    @Override
    public boolean update(Comment entity) {
        Document query = new Document("_id", new ObjectId(entity.getId()));

        Document update = new Document("$set",
                new Document("content", entity.getContent())
                        .append("updatedAt", System.currentTimeMillis())
        );

        return commentsCollection.updateOne(query, update).getModifiedCount() > 0;
    }

    @Override
    public boolean deleteById(String id) {
        Document query = new Document("_id", new ObjectId(id));
        return commentsCollection.deleteOne(query).getDeletedCount() > 0;
    }

    private Comment mapToComment(Document doc) {
        return new Comment(
                doc.getObjectId("_id").toHexString(),
                doc.getString("postId"),
                doc.getString("userId"),
                doc.getString("content"),
                doc.getLong("createdAt"),
                doc.getLong("updatedAt")
        );
    }
}
