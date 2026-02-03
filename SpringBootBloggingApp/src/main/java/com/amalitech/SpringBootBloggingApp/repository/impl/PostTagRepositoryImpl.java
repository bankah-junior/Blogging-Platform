package com.amalitech.SpringBootBloggingApp.repository.impl;

import com.amalitech.SpringBootBloggingApp.model.entity.PostTag;
import com.amalitech.SpringBootBloggingApp.model.entity.User;
import com.amalitech.SpringBootBloggingApp.repository.PostTagRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PostTagRepositoryImpl implements PostTagRepository {
    private final MongoTemplate mongoTemplate;

    public PostTagRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public PostTag save(PostTag entity) {
        return mongoTemplate.save(entity, "post_tags");
    }

    @Override
    public Optional<PostTag> findById(String id) {
        // PostTag doesn't have a single ID field, so we need to find by combination
        throw new UnsupportedOperationException("findById not supported for PostTag. Use findByPostIdAndTagId instead.");
    }

    @Override
    public List<PostTag> findAll() {
        return mongoTemplate.findAll(PostTag.class, "post_tags");
    }

    @Override
    public List<PostTag> findAll(int skip, int limit) {
        var query = new Query().skip((long) skip).limit(limit);
        return mongoTemplate.find(query, PostTag.class, "post_tags");
    }

    @Override
    public long count() {
        return mongoTemplate.count(new Query(), PostTag.class, "post_tags");
    }

    @Override
    public boolean update(PostTag entity) {
        var query = new Query(Criteria.where("postId").is(entity.getPostId()).and("tagId").is(entity.getTagId()));
        var update = new org.springframework.data.mongodb.core.query.Update()
                .set("postId", entity.getPostId())
                .set("tagId", entity.getTagId());
        return mongoTemplate.updateFirst(query, update, PostTag.class, "post_tags").wasAcknowledged();
    }

    @Override
    public boolean deleteById(String id) {
        // PostTag doesn't have a single ID field
        throw new UnsupportedOperationException("deleteById not supported for PostTag. Use deleteByPostIdAndTagId instead.");
    }

    @Override
    public List<PostTag> findByPostId(String postId) {
        var query = new Query(Criteria.where("postId").is(postId));
        return mongoTemplate.find(query, PostTag.class, "post_tags");
    }

    @Override
    public List<PostTag> findByTagId(String tagId) {
        var query = new Query(Criteria.where("tagId").is(tagId));
        return mongoTemplate.find(query, PostTag.class, "post_tags");
    }

    @Override
    public void deleteByPostId(String postId) {
        var query = new Query(Criteria.where("postId").is(postId));
        mongoTemplate.remove(query, PostTag.class, "post_tags");
    }

    @Override
    public void deleteByTagId(String tagId) {
        var query = new Query(Criteria.where("tagId").is(tagId));
        mongoTemplate.remove(query, PostTag.class, "post_tags");
    }

    @Override
    public boolean existsByPostIdAndTagId(String postId, String tagId) {
        var query = new Query(Criteria.where("postId").is(postId).and("tagId").is(tagId));
        return mongoTemplate.exists(query, PostTag.class, "post_tags");
    }
}
