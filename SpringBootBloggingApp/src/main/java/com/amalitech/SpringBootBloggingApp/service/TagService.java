package com.amalitech.SpringBootBloggingApp.service;

import com.amalitech.SpringBootBloggingApp.model.dto.request.CreateTagRequest;
import com.amalitech.SpringBootBloggingApp.model.dto.response.PageResponse;
import com.amalitech.SpringBootBloggingApp.model.entity.Tag;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TagService {

    Tag create(Tag tag);

    Tag create(CreateTagRequest request);

    @Transactional(readOnly = true)
    Tag getById(String tagId);

    Tag getByName(String name);

    List<Tag> getAll();

    PageResponse<Tag> getAllPaginated(int page, int size);

    void assignTagToPost(String postId, String tagId);

    List<Tag> getTagsByPost(String postId);

    void unassignAllTagsFromPost(String postId);

    void unassignTagFromPost(String postId, String tagId);
}
