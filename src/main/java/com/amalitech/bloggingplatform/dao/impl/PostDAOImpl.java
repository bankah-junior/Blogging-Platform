package com.amalitech.bloggingplatform.dao.impl;

import com.amalitech.bloggingplatform.dao.PostDAO;
import com.amalitech.bloggingplatform.model.Post;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.regex;

public class PostDAOImpl implements PostDAO {

    private final MongoCollection<Document> postsCollection;

    public PostDAOImpl(MongoDatabase database) {
        if (database == null) {
            throw new IllegalArgumentException("database must not be null");
        }
        this.postsCollection = database.getCollection("posts", Document.class);

        postsCollection.createIndex(new Document("authorId", 1));

        postsCollection.createIndex(new Document("title", "text"));

        postsCollection.createIndex(new Document("published", 1));
    }

    @Override
    public List<Post> findByAuthorId(String authorId) {
        List<Post> posts = new ArrayList<>();

        postsCollection.find(eq("authorId", authorId))
                .forEach(doc -> posts.add(mapToPost(doc)));

        return posts;
    }

    @Override
    public List<Post> searchByTitle(String keyword) {
        List<Post> posts = new ArrayList<>();

        postsCollection.find(regex("title", keyword, "i"))
                .forEach(doc -> posts.add(mapToPost(doc)));

        return posts;
    }

    @Override
    public Post save(Post entity) {

        Document doc = new Document("authorId", entity.getAuthorId())
                .append("title", entity.getTitle())
                .append("content", entity.getContent())
                .append("published", entity.isPublished())
                .append("createdAt", System.currentTimeMillis())
                .append("updatedAt", null);

        postsCollection.insertOne(doc);

        ObjectId id = doc.getObjectId("_id");
        if (id != null) {
            entity.setId(id.toHexString());
            return entity;
        }

        return null;
    }

    @Override
    public Optional<Post> findById(String id) {
        Document query = new Document("_id", new ObjectId(id));
        Document doc = postsCollection.find(query).first();

        if (doc != null) {
            return Optional.of(mapToPost(doc));
        }
        return Optional.empty();
    }

    @Override
    public List<Post> findAll() {
        List<Post> posts = new ArrayList<>();

        postsCollection.find()
                .forEach(doc -> posts.add(mapToPost(doc)));

        return posts;
    }

    @Override
    public boolean update(Post entity) {
        Document query = new Document("_id", new ObjectId(entity.getId()));

        Document update = new Document("$set",
                new Document("title", entity.getTitle())
                        .append("content", entity.getContent())
                        .append("published", entity.isPublished())
                        .append("updatedAt", System.currentTimeMillis())
        );

        return postsCollection.updateOne(query, update).getModifiedCount() > 0;
    }

    @Override
    public boolean deleteById(String id) {
        Document query = new Document("_id", new ObjectId(id));
        return postsCollection.deleteOne(query).getDeletedCount() > 0;
    }

    private Post mapToPost(Document doc) {
        return new Post(
                doc.getObjectId("_id").toHexString(),
                doc.getString("authorId"),
                doc.getString("title"),
                doc.getString("content"),
                doc.getBoolean("published", false),
                doc.getLong("createdAt"),
                doc.getLong("updatedAt")
        );
    }
}
