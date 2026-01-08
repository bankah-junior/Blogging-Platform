package com.amalitech.bloggingplatform.dao.impl;

import com.amalitech.bloggingplatform.dao.TagDAO;
import com.amalitech.bloggingplatform.model.Tag;

import java.util.List;
import java.util.Optional;

public class TagDAOImpl implements TagDAO {
    @Override
    public Tag save(Tag entity) {
        return null;
    }

    @Override
    public Optional<Tag> findById(String id) {
        return Optional.empty();
    }

    @Override
    public List<Tag> findAll() {
        return List.of();
    }

    @Override
    public boolean update(Tag entity) {
        return false;
    }

    @Override
    public boolean deleteById(String id) {
        return false;
    }
}
