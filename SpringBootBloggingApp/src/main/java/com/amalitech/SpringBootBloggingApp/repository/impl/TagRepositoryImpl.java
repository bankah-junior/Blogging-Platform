package com.amalitech.SpringBootBloggingApp.repository.impl;

import com.amalitech.SpringBootBloggingApp.model.entity.PostTag;
import com.amalitech.SpringBootBloggingApp.model.entity.Tag;
import com.amalitech.SpringBootBloggingApp.repository.TagRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TagRepositoryImpl implements TagRepository {
    private final MongoTemplate mongoTemplate;

    public TagRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Tag save(Tag entity) {
        return mongoTemplate.save(entity, "tags");
    }

    @Override
    public Optional<Tag> findById(String id) {
        var tag = mongoTemplate.findById(id, Tag.class, "tags");
        return Optional.ofNullable(tag);
    }

    @Override
    public List<Tag> findAll() {
        return mongoTemplate.findAll(Tag.class, "tags");
    }

    @Override
    public List<Tag> findAll(int skip, int limit) {
        var query = new Query().skip((long) skip).limit(limit);
        return mongoTemplate.find(query, Tag.class, "tags");
    }

    @Override
    public long count() {
        return mongoTemplate.count(new Query(), Tag.class, "tags");
    }

    @Override
    public boolean update(Tag entity) {
        var query = new Query(Criteria.where("id").is(entity.getId()));
        var update = new org.springframework.data.mongodb.core.query.Update()
                .set("name", entity.getName());
        return mongoTemplate.updateFirst(query, update, Tag.class, "tags").wasAcknowledged();
    }

    @Override
    public boolean deleteById(String id) {
        var query = new Query(Criteria.where("id").is(id));
        return mongoTemplate.remove(query, Tag.class, "tags").getDeletedCount() > 0;
    }

    @Override
    public Optional<Tag> findByName(String name) {
        var query = new Query(Criteria.where("name").is(name));
        return mongoTemplate.find(query, Tag.class, "tags").stream().findFirst();
    }

    @Override
    public void assignTagToPost(String postId, String tagId) {
        PostTag postTag = new PostTag(postId, tagId);
        mongoTemplate.save(postTag, "post_tags");
    }

    @Override
    public List<Tag> findTagsByPostId(String postId) {
        var linkQuery = new Query(Criteria.where("postId").is(postId));
        List<PostTag> links = mongoTemplate.find(linkQuery, PostTag.class, "post_tags");
        return links.stream()
                .map(link -> findById(link.getTagId()).orElse(null))
                .filter(tag -> tag != null)
                .toList();
    }

    @Override
    public void unassignTagFromPost(String postId, String tagId) {
        var query = new Query(Criteria.where("postId").is(postId).and("tagId").is(tagId));
        mongoTemplate.remove(query, "post_tags");
    }

    @Override
    public void unassignAllTagsFromPost(String postId) {
        var query = new Query(Criteria.where("postId").is(postId));
        mongoTemplate.remove(query, "post_tags");
    }
}
