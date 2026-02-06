package com.amalitech.SpringBootBloggingApp.repository.impl;

import com.amalitech.SpringBootBloggingApp.model.entity.Comment;
import com.amalitech.SpringBootBloggingApp.model.entity.Post;
import com.amalitech.SpringBootBloggingApp.model.entity.User;
import com.amalitech.SpringBootBloggingApp.repository.CommentRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CommentRepositoryImpl implements CommentRepository {
    private final MongoTemplate mongoTemplate;

    public CommentRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Comment save(Comment entity) {
        return mongoTemplate.save(entity, "comments");
    }

    @Override
    public Optional<Comment> findById(String id) {
        var comment = mongoTemplate.findById(id, Comment.class, "comments");
        return Optional.ofNullable(comment);
    }

    @Override
    public List<Comment> findAll() {
        return mongoTemplate.findAll(Comment.class, "comments");
    }

    @Override
    public List<Comment> findAll(int skip, int limit) {
        var query = new Query().skip((long) skip).limit(limit);
        return mongoTemplate.find(query, Comment.class, "comments");
    }

    @Override
    public long count() {
        return mongoTemplate.count(new Query(), Comment.class, "comments");
    }

    @Override
    public boolean update(Comment entity) {
        var query = new Query(Criteria.where("id").is(entity.getId()));
        var update = new org.springframework.data.mongodb.core.query.Update()
                .set("content", entity.getContent());
        return mongoTemplate.updateFirst(query, update, Comment.class, "comments").wasAcknowledged();
    }

    @Override
    public boolean deleteById(String id) {
        var query = new Query(Criteria.where("id").is(id));
        return mongoTemplate.remove(query, Comment.class, "comments").getDeletedCount() > 0;
    }

    @Override
    public List<Comment> findByPostId(String postId) {
        var query = new Query(Criteria.where("postId").is(postId));
        return mongoTemplate.find(query, Comment.class, "comments");
    }

    @Override
    public List<Comment> findByPost(Post post) {
        return List.of();
    }

    public List<Comment> findByUserId(String userId) {
        var query = new Query(Criteria.where("userId").is(userId));
        return mongoTemplate.find(query, Comment.class, "comments");
    }

    @Override
    public List<Comment> findByUser(User user) {
        return List.of();
    }
}
