package com.amalitech.SpringBootBloggingApp.service.impl;

import com.amalitech.SpringBootBloggingApp.cache.Cache;
import com.amalitech.SpringBootBloggingApp.model.dto.request.CreateTagRequest;
import com.amalitech.SpringBootBloggingApp.model.dto.response.PageResponse;
import com.amalitech.SpringBootBloggingApp.model.entity.Tag;
import com.amalitech.SpringBootBloggingApp.model.entity.User;
import com.amalitech.SpringBootBloggingApp.repository.impl.TagRepositoryImpl;
import com.amalitech.SpringBootBloggingApp.service.TagService;

import com.amalitech.SpringBootBloggingApp.util.ValidationUtil;
import com.amalitech.SpringBootBloggingApp.util.exceptions.UserInputsException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagServiceImpl implements TagService {
    private final TagRepositoryImpl tagRepository;
    private final Cache<String, User> userCache;

    public TagServiceImpl(TagRepositoryImpl tagRepository, Cache<String, User> userCache) {
        this.tagRepository = tagRepository;
        this.userCache = userCache;
    }

    @Override
    public Tag create(CreateTagRequest request) {
        if (!ValidationUtil.isValidTagName(request.getName())) {
            throw new UserInputsException("Tag name is not valid");
        }
        Tag tag = new Tag();
        tag.setName(request.getName());
        return tagRepository.save(tag);
    }

    @Override
    public Tag create(Tag tag) {
        if (!ValidationUtil.isValidTagName(tag.getName())) {
            throw new UserInputsException("Tag name is not valid");
        }
        return tagRepository.save(tag);
    }

    @Override
    public Tag getByName(String name) {
        if (!ValidationUtil.isValidTagName(name)) {
            throw new UserInputsException("Tag name is not valid");
        }
        return tagRepository.findByName(name).orElse(null);
    }

    @Override
    public List<Tag> getAll() {
        return tagRepository.findAll();
    }

    @Override
    public PageResponse<Tag> getAllPaginated(int page, int size) {
        long total = tagRepository.count();
        int skip = page * size;
        var content = tagRepository.findAll(skip, size);
        return new PageResponse<>(content, page, size, total);
    }

    @Override
    public void assignTagToPost(String postId, String tagId) {
        if (!ValidationUtil.isValidObjectId(postId)) {
            throw new UserInputsException("Post ID is not valid");
        }
        if (!ValidationUtil.isValidObjectId(tagId)) {
            throw new UserInputsException("Tag ID is not valid");
        }
        tagRepository.assignTagToPost(postId, tagId);
    }

    @Override
    public List<Tag> getTagsByPost(String postId) {
        if (!ValidationUtil.isValidObjectId(postId)) {
            throw new UserInputsException("Post ID is not valid");
        }
        return tagRepository.findTagsByPostId(postId);
    }

    @Override
    public void unassignAllTagsFromPost(String postId) {
        if (!ValidationUtil.isValidObjectId(postId)) {
            throw new UserInputsException("Post ID is not valid");
        }
        tagRepository.unassignAllTagsFromPost(postId);
    }

    @Override
    public void unassignTagFromPost(String postId, String tagId) {
        if (!ValidationUtil.isValidObjectId(postId)) {
            throw new UserInputsException("Post ID is not valid");
        }
        if (!ValidationUtil.isValidObjectId(tagId)) {
            throw new UserInputsException("Tag ID is not valid");
        }
        tagRepository.unassignTagFromPost(postId, tagId);
    }
}
