package com.amalitech.bloggingplatform.dao;

import com.amalitech.bloggingplatform.model.Tag;

import java.util.List;
import java.util.Optional;

public interface TagDAO extends BaseDAO<Tag> {

    Optional<Tag> findByName(String name);

    void assignTagToPost(String postId, String tagId);

    List<Tag> findTagsByPostId(String postId);

    void unassignAllTagsFromPost(String postId);
}
