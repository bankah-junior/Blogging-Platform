package com.amalitech.bloggingplatform.dao.impl;

import com.amalitech.bloggingplatform.dao.UserDAO;
import com.amalitech.bloggingplatform.model.User;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.amalitech.bloggingplatform.utils.PasswordUtils.hashPassword;
import static com.amalitech.bloggingplatform.utils.PasswordUtils.verifyPassword;
import static com.mongodb.client.model.Filters.eq;

public class UserDAOImpl implements UserDAO {

    private final MongoCollection<Document> usersCollection;

    public UserDAOImpl(MongoDatabase database) {
        if (database == null) {
            throw new IllegalArgumentException("database must not be null");
        }
        this.usersCollection = database.getCollection("users", Document.class);

        usersCollection.createIndex(
                new Document("username", 1),
                new IndexOptions().unique(true)
        );

        usersCollection.createIndex(
                new Document("email", 1),
                new IndexOptions().unique(true)
        );
    }

    @Override
    public Optional<User> findByUsername(String username) {
        Document doc = usersCollection.find(eq("username", username)).first();
        return doc != null ? Optional.of(mapToUser(doc)) : Optional.empty();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        Document doc = usersCollection.find(eq("email", email)).first();
        return doc != null ? Optional.of(mapToUser(doc)) : Optional.empty();
    }

    @Override
    public User save(User entity) {

        Document doc = new Document("username", entity.getUsername())
                .append("email", entity.getEmail())
                .append("passwordHash", hashPassword(entity.getPasswordHash()))
                .append("createdAt", System.currentTimeMillis());

        usersCollection.insertOne(doc);

        ObjectId id = doc.getObjectId("_id");
        if (id != null) {
            entity.setId(id.toHexString());
            return entity;
        }
        return null;
    }

    @Override
    public Optional<User> login(String email, String password) {
        Document doc = usersCollection.find(eq("email", email)).first();
        if(doc != null){
            String hashedPassword = doc.getString("passwordHash");
            if(verifyPassword(password, hashedPassword)){
                return Optional.of(mapToUser(doc));
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findById(String id) {
        Document doc = usersCollection
                .find(eq("_id", new ObjectId(id)))
                .first();

        return doc != null ? Optional.of(mapToUser(doc)) : Optional.empty();
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();

        usersCollection.find()
                .forEach(doc -> users.add(mapToUser(doc)));

        return users;
    }

    @Override
    public boolean update(User entity) {

        Document update = new Document("$set",
                new Document("username", entity.getUsername())
                        .append("email", entity.getEmail())
                        .append("passwordHash", hashPassword(entity.getPasswordHash()))
                        .append("updatedAt", System.currentTimeMillis())
        );

        return usersCollection.updateOne(
                eq("_id", new ObjectId(entity.getId())),
                update
        ).getModifiedCount() > 0;
    }

    @Override
    public boolean deleteById(String id) {
        return usersCollection
                .deleteOne(eq("_id", new ObjectId(id)))
                .getDeletedCount() > 0;
    }

    private User mapToUser(Document doc) {
        return new User(
                doc.getObjectId("_id").toHexString(),
                doc.getString("username"),
                doc.getString("email"),
                doc.getString("passwordHash"),
                doc.getLong("createdAt"),
                doc.getLong("updatedAt")
        );
    }
}
