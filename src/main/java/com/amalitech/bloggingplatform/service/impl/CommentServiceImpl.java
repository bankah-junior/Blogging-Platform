package com.amalitech.bloggingplatform.service.impl;

import com.amalitech.bloggingplatform.dao.CommentDAO;
import com.amalitech.bloggingplatform.dao.impl.CommentDAOImpl;
import com.amalitech.bloggingplatform.model.Comment;
import com.amalitech.bloggingplatform.service.CommentService;
import com.amalitech.bloggingplatform.utils.MongoDBConnection;
import com.mongodb.client.MongoDatabase;

import java.util.List;

public class CommentServiceImpl implements CommentService {

    private final CommentDAO commentDAO;

    public CommentServiceImpl() {
        MongoDatabase database = MongoDBConnection.getDatabase();
        this.commentDAO = new CommentDAOImpl(database);
    }

    @Override
    public Comment create(Comment comment) {
        return commentDAO.save(comment);
    }

    @Override
    public boolean delete(String commentId) {
        return commentDAO.deleteById(commentId);
    }

    @Override
    public List<Comment> getByPost(String postId) {
        return commentDAO.findByPostId(postId);
    }

    @Override
    public List<Comment> getByUser(String userId) {
        // Note: This would need a findByUserId method in DAO
        return List.of();
    }
}