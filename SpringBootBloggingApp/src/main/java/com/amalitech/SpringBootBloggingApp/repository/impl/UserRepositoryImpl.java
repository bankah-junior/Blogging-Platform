package com.amalitech.SpringBootBloggingApp.repository.impl;

import com.amalitech.SpringBootBloggingApp.model.entity.User;
import com.amalitech.SpringBootBloggingApp.repository.UserRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private final MongoTemplate mongoTemplate;

    public UserRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public User save(User entity) {
        return mongoTemplate.save(entity, "users");
    }

    @Override
    public Optional<User> findById(String id) {
        User user = mongoTemplate.findById(id, User.class, "users");
        return Optional.ofNullable(user);
    }

    @Override
    public List<User> findAll() {
        return mongoTemplate.findAll(User.class, "users");
    }

    @Override
    public List<User> findAll(int skip, int limit) {
        var query = new Query().skip((long) skip).limit(limit);
        return mongoTemplate.find(query, User.class, "users");
    }

    @Override
    public long count() {
        return mongoTemplate.count(new Query(), User.class, "users");
    }

    @Override
    public boolean update(User entity) {
        var query = new Query(Criteria.where("id").is(entity.getId()));
        var update = new org.springframework.data.mongodb.core.query.Update()
                .set("username", entity.getUsername())
                .set("email", entity.getEmail())
                .set("passwordHash", entity.getPasswordHash());
        return mongoTemplate.updateFirst(query, update, User.class, "users").wasAcknowledged();
    }

    @Override
    public boolean deleteById(String id) {
        var query = new Query(Criteria.where("id").is(id));
        return mongoTemplate.remove(query, User.class, "users").getDeletedCount() > 0;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        var query = new Query(Criteria.where("username").is(username));
        return mongoTemplate.find(query, User.class, "users").stream().findFirst();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        var query = new Query(Criteria.where("email").is(email));
        return mongoTemplate.find(query, User.class, "users").stream().findFirst();
    }

    @Override
    public Optional<User> login(String email, String password) {
        var query = new Query(Criteria.where("email").is(email).and("passwordHash").is(password));
        return mongoTemplate.find(query, User.class, "users").stream().findFirst();
    }

    @Override
    public boolean updateUserDetails(String userId, User user) {
        var query = new Query(Criteria.where("id").is(userId));
        var update = new org.springframework.data.mongodb.core.query.Update()
                .set("username", user.getUsername())
                .set("email", user.getEmail());
        return mongoTemplate.updateFirst(query, update, User.class, "users").wasAcknowledged();
    }

    @Override
    public boolean updatePassword(String userId, String newPasswordHash) {
        var query = new Query(Criteria.where("id").is(userId));
        var update = new org.springframework.data.mongodb.core.query.Update()
                .set("passwordHash", newPasswordHash);
        return mongoTemplate.updateFirst(query, update, User.class, "users").wasAcknowledged();
    }
}

