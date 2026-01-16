package com.amalitech.bloggingplatform.service;

import com.amalitech.bloggingplatform.model.Tag;

import java.util.List;

public interface TagService {

    Tag create(Tag tag);

    Tag getByName(String name);

    List<Tag> getAll();

    void assignTagToPost(String postId, String tagId);

    List<Tag> getTagsByPost(String postId);

    void unassignAllTagsFromPost(String postId);
}
