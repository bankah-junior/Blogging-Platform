package com.amalitech.bloggingplatform.service.impl;

import com.amalitech.bloggingplatform.dao.TagDAO;
import com.amalitech.bloggingplatform.model.Tag;
import com.amalitech.bloggingplatform.service.TagService;

import java.util.List;

import com.amalitech.bloggingplatform.dao.impl.TagDAOImpl;
import com.amalitech.bloggingplatform.utils.MongoDBConnection;
import com.mongodb.client.MongoDatabase;

import java.util.List;

public class TagServiceImpl implements TagService {

    private final TagDAO tagDao;

    public TagServiceImpl(TagDAO tagDao) {
        this.tagDao = tagDao;
    }

    public TagServiceImpl() {
        MongoDatabase database = MongoDBConnection.getDatabase();
        this.tagDao = new TagDAOImpl(database);
    }

    @Override
    public Tag create(Tag tag) {
        return tagDao.save(tag);
    }

    @Override
    public Tag getByName(String name) {
        return tagDao.findByName(name).orElse(null);
    }

    @Override
    public List<Tag> getAll() {
        return tagDao.findAll();
    }

    @Override
    public void assignTagToPost(String postId, String tagId) {
        tagDao.assignTagToPost(postId, tagId);
    }

    @Override
    public List<Tag> getTagsByPost(String postId) {
        return tagDao.findTagsByPostId(postId);
    }

    @Override
    public void unassignAllTagsFromPost(String postId) {
        tagDao.unassignAllTagsFromPost(postId);
    }
}    