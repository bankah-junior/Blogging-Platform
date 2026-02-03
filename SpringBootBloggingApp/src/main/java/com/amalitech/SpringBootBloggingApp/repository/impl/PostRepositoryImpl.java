package com.amalitech.SpringBootBloggingApp.repository.impl;

import com.amalitech.SpringBootBloggingApp.model.entity.Post;
import com.amalitech.SpringBootBloggingApp.repository.PostRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PostRepositoryImpl implements PostRepository {
    private final MongoTemplate mongoTemplate;

    public PostRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Post save(Post entity) {
        return mongoTemplate.save(entity, "posts");
    }

    @Override
    public Optional<Post> findById(String id) {
        var post = mongoTemplate.findById(id, Post.class, "posts");
        return Optional.ofNullable(post);
    }

    @Override
    public List<Post> findAll() {
        return mongoTemplate.findAll(Post.class, "posts");
    }

    @Override
    public List<Post> findAll(int skip, int limit) {
        var query = new Query().skip((long) skip).limit(limit);
        return mongoTemplate.find(query, Post.class, "posts");
    }

    @Override
    public long count() {
        return mongoTemplate.count(new Query(), Post.class, "posts");
    }

    @Override
    public boolean update(Post entity) {
        var query = new Query(Criteria.where("id").is(entity.getId()));
        var update = new org.springframework.data.mongodb.core.query.Update()
                .set("title", entity.getTitle())
                .set("content", entity.getContent())
                .set("author", entity.getAuthor())
                .set("published", entity.isPublished())
                .set("updatedAt", entity.getUpdatedAt());
        return mongoTemplate.updateFirst(query, update, Post.class, "posts").wasAcknowledged();
    }

    @Override
    public boolean deleteById(String id) {
        var query = new Query(Criteria.where("id").is(id));
        return mongoTemplate.remove(query, Post.class, "posts").getDeletedCount() > 0;
    }

    @Override
    public List<Post> findByAuthorId(String authorId) {
        var query = new Query(Criteria.where("authorId").is(authorId));
        return mongoTemplate.find(query, Post.class, "posts");
    }

    @Override
    public List<Post> searchByTitle(String keyword) {
        var query = new Query(Criteria.where("title").regex(".*" + keyword + ".*", "i"));
        return mongoTemplate.find(query, Post.class, "posts");
    }

    @Override
    public List<Post> findByTagName(String tagName) {
        return List.of();
    }

    @Override
    public List<Post> findByAuthor(com.amalitech.SpringBootBloggingApp.model.entity.User author) {
        var query = new Query(Criteria.where("author.$id").is(author.getId()));
        return mongoTemplate.find(query, Post.class, "posts");
    }

    @Override
    public List<Post> findByPublished(boolean published) {
        var query = new Query(Criteria.where("published").is(published));
        return mongoTemplate.find(query, Post.class, "posts");
    }
}
