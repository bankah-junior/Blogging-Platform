package com.amalitech.SpringBootBloggingApp.service.impl;

import com.amalitech.SpringBootBloggingApp.model.dto.DtoMapper;
import com.amalitech.SpringBootBloggingApp.model.dto.response.PageResponse;
import com.amalitech.SpringBootBloggingApp.model.dto.response.PostTagResponse;
import com.amalitech.SpringBootBloggingApp.model.entity.PostTag;
import com.amalitech.SpringBootBloggingApp.repository.PostTagRepository;
import com.amalitech.SpringBootBloggingApp.service.PostTagService;
import com.amalitech.SpringBootBloggingApp.util.ValidationUtil;
import com.amalitech.SpringBootBloggingApp.util.exceptions.UserInputsException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostTagServiceImpl implements PostTagService {
    
    private final PostTagRepository postTagRepository;
    
    public PostTagServiceImpl(PostTagRepository postTagRepository) {
        this.postTagRepository = postTagRepository;
    }
    
    @Override
    public PostTagResponse create(PostTag postTag) {
        if (postTag.getPostId() == null || postTag.getTagId() == null) {
            throw new UserInputsException("Post ID and Tag ID are required");
        }
        if (!ValidationUtil.isValidObjectId(postTag.getPostId()) || !ValidationUtil.isValidObjectId(postTag.getTagId())) {
            throw new UserInputsException("Invalid Post ID or Tag ID");
        }
        if (postTagRepository.existsByPostIdAndTagId(postTag.getPostId(), postTag.getTagId())) {
            throw new UserInputsException("Tag already assigned to this post");
        }
        return DtoMapper.toPostTagResponse(postTagRepository.save(postTag));
    }
    
    @Override
    public boolean delete(String postId, String tagId) {
        if (!ValidationUtil.isValidObjectId(postId) || !ValidationUtil.isValidObjectId(tagId)) {
            throw new UserInputsException("Invalid Post ID or Tag ID");
        }
        PostTag postTag = new PostTag(postId, tagId);
        return postTagRepository.update(postTag);
    }
    
    @Override
    public List<PostTagResponse> getByPostId(String postId) {
        if (!ValidationUtil.isValidObjectId(postId)) {
            throw new UserInputsException("Invalid Post ID");
        }
        return DtoMapper.toPostTagResponses(postTagRepository.findByPostId(postId));
    }
    
    @Override
    public List<PostTagResponse> getByTagId(String tagId) {
        if (!ValidationUtil.isValidObjectId(tagId)) {
            throw new UserInputsException("Invalid Tag ID");
        }
        return DtoMapper.toPostTagResponses(postTagRepository.findByTagId(tagId));
    }
    
    @Override
    public void assignTagToPost(String postId, String tagId) {
        if (!ValidationUtil.isValidObjectId(postId) || !ValidationUtil.isValidObjectId(tagId)) {
            throw new UserInputsException("Invalid Post ID or Tag ID");
        }
        if (postTagRepository.existsByPostIdAndTagId(postId, tagId)) {
            throw new UserInputsException("Tag already assigned to this post");
        }
        PostTag postTag = new PostTag(postId, tagId);
        postTagRepository.save(postTag);
    }
    
    @Override
    public void unassignTagFromPost(String postId, String tagId) {
        if (!ValidationUtil.isValidObjectId(postId) || !ValidationUtil.isValidObjectId(tagId)) {
            throw new UserInputsException("Invalid Post ID or Tag ID");
        }
        // Find and delete the specific PostTag
        List<PostTag> postTags = postTagRepository.findByPostId(postId);
        for (PostTag postTag : postTags) {
            if (postTag.getTagId().equals(tagId)) {
                postTagRepository.deleteByPostId(postId);
                break;
            }
        }
    }
    
    @Override
    public void unassignAllTagsFromPost(String postId) {
        if (!ValidationUtil.isValidObjectId(postId)) {
            throw new UserInputsException("Invalid Post ID");
        }
        postTagRepository.deleteByPostId(postId);
    }
    
    @Override
    public boolean exists(String postId, String tagId) {
        if (!ValidationUtil.isValidObjectId(postId) || !ValidationUtil.isValidObjectId(tagId)) {
            throw new UserInputsException("Invalid Post ID or Tag ID");
        }
        return postTagRepository.existsByPostIdAndTagId(postId, tagId);
    }
    
    @Override
    public List<PostTag> getAllPaginated() {
        return postTagRepository.findAll();
    }

    @Override
    public PageResponse<PostTag> getAllPaginated(int page, int size) {
        long total = postTagRepository.count();
        int skip = page * size;
        var content = postTagRepository.findAll(skip, size);
        return new PageResponse<>(content, page, size, total);
    }
}
