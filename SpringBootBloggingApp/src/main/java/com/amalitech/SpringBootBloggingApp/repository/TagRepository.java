package com.amalitech.SpringBootBloggingApp.repository;

import com.amalitech.SpringBootBloggingApp.model.entity.Tag;

import java.util.List;
import java.util.Optional;

public interface TagRepository extends BaseRepository<Tag> {

    Optional<Tag> findByName(String name);

    void assignTagToPost(String postId, String tagId);

    List<Tag> findTagsByPostId(String postId);

    void unassignAllTagsFromPost(String postId);

    void unassignTagFromPost(String postId, String tagId);
}
