package com.amalitech.bloggingplatform.dao.impl;

import com.amalitech.bloggingplatform.dao.ReviewDAO;
import com.amalitech.bloggingplatform.model.Review;

import java.util.List;
import java.util.Optional;

public class ReviewDAOImpl implements ReviewDAO {
    @Override
    public Review save(Review entity) {
        return null;
    }

    @Override
    public Optional<Review> findById(String id) {
        return Optional.empty();
    }

    @Override
    public List<Review> findAll() {
        return List.of();
    }

    @Override
    public boolean update(Review entity) {
        return false;
    }

    @Override
    public boolean deleteById(String id) {
        return false;
    }
}
