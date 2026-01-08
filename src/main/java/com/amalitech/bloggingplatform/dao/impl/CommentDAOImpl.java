package com.amalitech.bloggingplatform.dao.impl;

import com.amalitech.bloggingplatform.dao.CommentDAO;
import com.amalitech.bloggingplatform.model.Comment;

import java.util.List;
import java.util.Optional;

public class CommentDAOImpl implements CommentDAO {
    @Override
    public Comment save(Comment entity) {
        return null;
    }

    @Override
    public Optional<Comment> findById(String id) {
        return Optional.empty();
    }

    @Override
    public List<Comment> findAll() {
        return List.of();
    }

    @Override
    public boolean update(Comment entity) {
        return false;
    }

    @Override
    public boolean deleteById(String id) {
        return false;
    }
}
