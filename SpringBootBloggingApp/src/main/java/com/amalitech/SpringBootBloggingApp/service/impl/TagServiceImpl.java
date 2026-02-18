package com.amalitech.SpringBootBloggingApp.service.impl;

import com.amalitech.SpringBootBloggingApp.cache.Cache;
import com.amalitech.SpringBootBloggingApp.model.dto.request.CreateTagRequest;
import com.amalitech.SpringBootBloggingApp.model.dto.response.PageResponse;
import com.amalitech.SpringBootBloggingApp.model.entity.Tag;
import com.amalitech.SpringBootBloggingApp.model.entity.User;
import com.amalitech.SpringBootBloggingApp.repository.PostTagRepository;
import com.amalitech.SpringBootBloggingApp.repository.TagRepository;
import com.amalitech.SpringBootBloggingApp.service.TagService;
import com.amalitech.SpringBootBloggingApp.util.ValidationUtil;
import com.amalitech.SpringBootBloggingApp.util.exceptions.UserInputsException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TagServiceImpl implements TagService {
    private final TagRepository tagRepository;
    private final PostTagRepository postTagRepository;
    private final Cache<String, User> userCache;

    public TagServiceImpl(TagRepository tagRepository, PostTagRepository postTagRepository, Cache<String, User> userCache) {
        this.tagRepository = tagRepository;
        this.postTagRepository = postTagRepository;
        this.userCache = userCache;
    }

    @Override
    @Transactional
    public Tag create(CreateTagRequest request) {
        if (!ValidationUtil.isValidTagName(request.getName())) {
            throw new UserInputsException("Tag name is not valid");
        }
        Tag tag = new Tag();
        tag.setName(request.getName());
        return tagRepository.save(tag);
    }

    @Override
    @Transactional
    @CacheEvict(value = "tags", allEntries = true)
    public Tag create(Tag tag) {
        if (!ValidationUtil.isValidTagName(tag.getName())) {
            throw new UserInputsException("Tag name is not valid");
        }
        return tagRepository.save(tag);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "tags", key = "#tagId")
    @Override
    public Tag getById(String tagId) {
        if (!ValidationUtil.isValidObjectId(tagId)) {
            throw new UserInputsException("Tag ID is not valid");
        }
        return tagRepository.findById(tagId).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "tags", key = "#name")
    public Tag getByName(String name) {
        if (!ValidationUtil.isValidTagName(name)) {
            throw new UserInputsException("Tag name is not valid");
        }
        return tagRepository.findByName(name).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "tags", key = "'all'")
    public List<Tag> getAll() {
        return tagRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<Tag> getAllPaginated(int page, int size) {
        long total = tagRepository.count();
        var pageable = PageRequest.of(page, size);
        var pageResult = tagRepository.findAll(pageable);
        return new PageResponse<>(pageResult.getContent(), page, size, total);
    }

    @Override
    @Transactional
    @CacheEvict(value = "tags", allEntries = true)
    public void assignTagToPost(String postId, String tagId) {
        if (!ValidationUtil.isValidObjectId(postId)) {
            throw new UserInputsException("Post ID is not valid");
        }
        if (!ValidationUtil.isValidObjectId(tagId)) {
            throw new UserInputsException("Tag ID is not valid");
        }
        // Use PostTagRepository for tag assignments
        com.amalitech.SpringBootBloggingApp.model.entity.PostTag postTag = 
            new com.amalitech.SpringBootBloggingApp.model.entity.PostTag();
        postTag.setPostId(postId);
        postTag.setTagId(tagId);
        postTagRepository.save(postTag);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "tags", key = "#postId")
    public List<Tag> getTagsByPost(String postId) {
        if (!ValidationUtil.isValidObjectId(postId)) {
            throw new UserInputsException("Post ID is not valid");
        }
        var postTags = postTagRepository.findByPostId(postId);
        return postTags.stream()
            .map(pt -> tagRepository.findById(pt.getTagId()).orElse(null))
            .filter(tag -> tag != null)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    @CacheEvict(value = "tags", allEntries = true)
    public void unassignAllTagsFromPost(String postId) {
        if (!ValidationUtil.isValidObjectId(postId)) {
            throw new UserInputsException("Post ID is not valid");
        }
        postTagRepository.deleteByPostId(postId);
    }

    @Override
    @Transactional
    @CacheEvict(value = "tags", allEntries = true)
    public void unassignTagFromPost(String postId, String tagId) {
        if (!ValidationUtil.isValidObjectId(postId)) {
            throw new UserInputsException("Post ID is not valid");
        }
        if (!ValidationUtil.isValidObjectId(tagId)) {
            throw new UserInputsException("Tag ID is not valid");
        }
        postTagRepository.deleteByPostIdAndTagId(postId, tagId);
    }
}
