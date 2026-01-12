package com.amalitech.bloggingplatform.dao.impl;

import com.amalitech.bloggingplatform.dao.TagDAO;
import com.amalitech.bloggingplatform.model.Tag;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.mongodb.client.model.Filters.eq;

public class TagDAOImpl implements TagDAO {

    private final MongoCollection<Document> tagsCollection;
    private final MongoCollection<Document> postTagsCollection;

    public TagDAOImpl(MongoDatabase database) {
        if (database == null) {
            throw new IllegalArgumentException("database must not be null");
        }
        this.tagsCollection = database.getCollection("tags", Document.class);
        this.postTagsCollection = database.getCollection("post_tags", Document.class);

        tagsCollection.createIndex(new Document("name", 1));
    }

    @Override
    public Optional<Tag> findByName(String name) {
        Document doc = tagsCollection.find(eq("name", name)).first();
        return doc != null ? Optional.of(mapToTag(doc)) : Optional.empty();
    }

    @Override
    public void assignTagToPost(String postId, String tagId) {

        Document mapping = new Document("postId", postId)
                .append("tagId", tagId);

        postTagsCollection.insertOne(mapping);
    }

    @Override
    public List<Tag> findTagsByPostId(String postId) {
        List<Tag> tags = new ArrayList<>();

        postTagsCollection.find(eq("postId", postId))
                .forEach(mapping -> {
                    String tagId = mapping.getString("tagId");
                    findById(tagId).ifPresent(tags::add);
                });

        return tags;
    }

    @Override
    public Tag save(Tag entity) {

        Document doc = new Document("name", entity.getName());
        tagsCollection.insertOne(doc);

        ObjectId id = doc.getObjectId("_id");
        if (id != null) {
            entity.setId(id.toHexString());
            return entity;
        }
        return null;
    }

    @Override
    public Optional<Tag> findById(String id) {
        Document doc = tagsCollection.find(eq("_id", new ObjectId(id))).first();
        return doc != null ? Optional.of(mapToTag(doc)) : Optional.empty();
    }

    @Override
    public List<Tag> findAll() {
        List<Tag> tags = new ArrayList<>();

        tagsCollection.find()
                .forEach(doc -> tags.add(mapToTag(doc)));

        return tags;
    }

    @Override
    public boolean update(Tag entity) {
        Document query = new Document("_id", new ObjectId(entity.getId()));
        Document update = new Document("$set",
                new Document("name", entity.getName())
        );

        return tagsCollection.updateOne(query, update).getModifiedCount() > 0;
    }

    @Override
    public boolean deleteById(String id) {
        Document query = new Document("_id", new ObjectId(id));

        // Remove tag mappings first
        postTagsCollection.deleteMany(eq("tagId", id));

        return tagsCollection.deleteOne(query).getDeletedCount() > 0;
    }

    private Tag mapToTag(Document doc) {
        return new Tag(
                doc.getObjectId("_id").toHexString(),
                doc.getString("name")
        );
    }
}
