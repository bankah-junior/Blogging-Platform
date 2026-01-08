package com.amalitech.bloggingplatform.dao.impl;

import com.amalitech.bloggingplatform.dao.PostDAO;
import com.amalitech.bloggingplatform.model.Post;

import java.util.List;
import java.util.Optional;

public class PostDAOImpl implements PostDAO {
    @Override
    public Post save(Post entity) {
        return null;
    }

    @Override
    public Optional<Post> findById(String id) {
        return Optional.empty();
    }

    @Override
    public List<Post> findAll() {
        return List.of();
    }

    @Override
    public boolean update(Post entity) {
        return false;
    }

    @Override
    public boolean deleteById(String id) {
        return false;
    }
}
