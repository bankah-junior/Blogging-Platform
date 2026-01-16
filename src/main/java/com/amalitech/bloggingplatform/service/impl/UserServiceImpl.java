package com.amalitech.bloggingplatform.service.impl;

import com.amalitech.bloggingplatform.cache.Cache;
import com.amalitech.bloggingplatform.cache.LruCache;
import com.amalitech.bloggingplatform.dao.UserDAO;
import com.amalitech.bloggingplatform.dao.impl.UserDAOImpl;
import com.amalitech.bloggingplatform.model.User;
import com.amalitech.bloggingplatform.service.UserService;
import com.amalitech.bloggingplatform.utils.MongoDBConnection;
import com.mongodb.client.MongoDatabase;

import java.util.List;
import java.util.Optional;

public class UserServiceImpl implements UserService {

    private final UserDAO userDAO;
    private final Cache<String, User> userCache;

    public UserServiceImpl() {
        MongoDatabase database = MongoDBConnection.getDatabase();
        this.userDAO = new UserDAOImpl(database);
        this.userCache = new LruCache<>(50);
    }

    @Override
    public User create(User user) {
        User saved = userDAO.save(user);
        if (saved != null) {
            userCache.put(saved.getId(), saved);
        }
        return saved;
    }

    @Override
    public User login(String email, String password) {
        return userDAO.login(email, password).orElse(null);
    }

    @Override
    public User update(User user) {
        if (userDAO.update(user)) {
            userCache.put(user.getId(), user);
            return user;
        }
        return null;
    }

    @Override
    public boolean delete(String userId) {
        boolean deleted = userDAO.deleteById(userId);
        if (deleted) {
            userCache.remove(userId);
        }
        return deleted;
    }

    @Override
    public User getById(String userId) {
        return userDAO.findById(userId).orElse(null);
    }

    @Override
    public User getByEmail(String email) {
        return userDAO.findByEmail(email).orElse(null);
    }

    @Override
    public List<User> getAll() {
        return userDAO.findAll();
    }

    @Override
    public boolean updateUserDetails(User user) {
        if (userDAO.updateUserDetails(user)) {
            // In a real app, you might need to fetch the updated user object
            // to ensure the cache is not stale, as the user object passed in
            // might not be the fully updated one from the DB (e.g. updatedAt).
            userCache.put(user.getId(), user);
            return true;
        }
        return false;
    }

    @Override
    public boolean changePassword(String userId, String oldPassword, String newPassword) {
        Optional<User> userOpt = userDAO.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (com.amalitech.bloggingplatform.utils.PasswordUtils.verifyPassword(oldPassword, user.getPasswordHash())) {
                String newPasswordHash = com.amalitech.bloggingplatform.utils.PasswordUtils.hashPassword(newPassword);
                if (userDAO.updatePassword(userId, newPasswordHash)) {
                    // Invalidate cache for this user
                    userCache.remove(userId);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public User getByUsername(String username) {
        return userDAO.findByUsername(username).orElse(null);
    }
}

