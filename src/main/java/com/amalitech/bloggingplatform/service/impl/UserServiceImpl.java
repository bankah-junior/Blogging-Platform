package com.amalitech.bloggingplatform.service.impl;

import com.amalitech.bloggingplatform.cache.Cache;
import com.amalitech.bloggingplatform.cache.LruCache;
import com.amalitech.bloggingplatform.dao.impl.UserDAOImpl;
import com.amalitech.bloggingplatform.model.User;
import com.amalitech.bloggingplatform.service.UserService;
import com.amalitech.bloggingplatform.utils.MongoDBConnection;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;

import java.util.List;
import java.util.Optional;

public class UserServiceImpl implements UserService {

    MongoClient client = MongoDBConnection.connect();
    private final Cache<String, User> userCache;

    public UserServiceImpl() {
        this.userCache = new LruCache<>(50);
    }

    @Override
    public User create(User user) {
        try{
            MongoDatabase db = client.getDatabase("java-demo");
            UserDAOImpl userDAO = new UserDAOImpl(db);
            User saved = userDAO.save(user);
            userCache.put(saved.getId(), saved);
            return saved;
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize UserServiceImpl", e);
        } finally {
            client.close(); // close only after all DB work is done
        }
    }

    @Override
    public User login(String email, String password) {
        try{
            MongoDatabase db = client.getDatabase("java-demo");
            UserDAOImpl userDAO = new UserDAOImpl(db);
            Optional<User> userOpt = userDAO.login(email, password);
            return userOpt.orElse(null);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize UserServiceImpl", e);
        } finally {
            client.close(); // close only after all DB work is done
        }
    }

    @Override
    public User update(User user) {
        try{
            MongoDatabase db = client.getDatabase("java-demo");
            UserDAOImpl userDAO = new UserDAOImpl(db);
            if(userDAO.update(user)){
                userCache.put(user.getId(), user);
                return user;
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize UserServiceImpl", e);
        } finally {
            client.close(); // close only after all DB work is done
        }
    }

    @Override
    public boolean delete(String userId) {
        try{
            MongoDatabase db = client.getDatabase("java-demo");
            UserDAOImpl userDAO = new UserDAOImpl(db);
            boolean deleted = userDAO.deleteById(userId);
            if (deleted) {
                userCache.remove(userId);
            }
            return deleted;
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize UserServiceImpl", e);
        } finally {
            client.close(); // close only after all DB work is done
        }
    }

    @Override
    public User getById(String userId) {
        try{
            MongoDatabase db = client.getDatabase("java-demo");
            UserDAOImpl userDAO = new UserDAOImpl(db);
            return userDAO.findById(userId).orElse(null);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize UserServiceImpl", e);
        } finally {
            client.close(); // close only after all DB work is done
        }
    }

    @Override
    public User getByEmail(String email) {
        try{
            MongoDatabase db = client.getDatabase("java-demo");
            UserDAOImpl userDAO = new UserDAOImpl(db);
            return userDAO.findByEmail(email).orElse(null);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize UserServiceImpl", e);
        } finally {
            client.close(); // close only after all DB work is done
        }
    }

    @Override
    public List<User> getAll() {
        try{
            MongoDatabase db = client.getDatabase("java-demo");
            UserDAOImpl userDAO = new UserDAOImpl(db);
            return userDAO.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize UserServiceImpl", e);
        } finally {
            client.close(); // close only after all DB work is done
        }
    }
}

