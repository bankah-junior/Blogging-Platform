package com.amalitech.bloggingplatform.service.impl;

import com.amalitech.bloggingplatform.dao.impl.CommentDAOImpl;
import com.amalitech.bloggingplatform.model.Comment;
import com.amalitech.bloggingplatform.service.CommentService;
import com.amalitech.bloggingplatform.utils.MongoDBConnection;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;

import java.util.List;

public class CommentServiceImpl implements CommentService {

    @Override
    public Comment create(Comment comment) {
        MongoClient client = MongoDBConnection.connect();
        try {
            MongoDatabase db = client.getDatabase("java-demo");
            CommentDAOImpl commentDAO = new CommentDAOImpl(db);
            return commentDAO.save(comment);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create comment", e);
        } finally {
            client.close();
        }
    }

    @Override
    public boolean delete(String commentId) {
        MongoClient client = MongoDBConnection.connect();
        try {
            MongoDatabase db = client.getDatabase("java-demo");
            CommentDAOImpl commentDAO = new CommentDAOImpl(db);
            return commentDAO.deleteById(commentId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete comment", e);
        } finally {
            client.close();
        }
    }

    @Override
    public List<Comment> getByPost(String postId) {
        MongoClient client = MongoDBConnection.connect();
        try {
            MongoDatabase db = client.getDatabase("java-demo");
            CommentDAOImpl commentDAO = new CommentDAOImpl(db);
            return commentDAO.findByPostId(postId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get comments by post", e);
        } finally {
            client.close();
        }
    }

    @Override
    public List<Comment> getByUser(String userId) {
        MongoClient client = MongoDBConnection.connect();
        try {
            MongoDatabase db = client.getDatabase("java-demo");
            CommentDAOImpl commentDAO = new CommentDAOImpl(db);
            // Note: This would need a findByUserId method in DAO
            return List.of();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get comments by user", e);
        } finally {
            client.close();
        }
    }
}
