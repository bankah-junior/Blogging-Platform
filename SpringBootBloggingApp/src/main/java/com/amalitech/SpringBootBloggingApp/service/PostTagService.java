package com.amalitech.SpringBootBloggingApp.service;

import com.amalitech.SpringBootBloggingApp.model.dto.response.PageResponse;
import com.amalitech.SpringBootBloggingApp.model.dto.response.PostTagResponse;
import com.amalitech.SpringBootBloggingApp.model.entity.PostTag;

import java.util.List;

public interface PostTagService {
    
    PostTagResponse create(PostTag postTag);

    boolean delete(String postId, String tagId);

    List<PostTagResponse> getByPostId(String postId);

    List<PostTagResponse> getByTagId(String tagId);
    
    void assignTagToPost(String postId, String tagId);
    
    void unassignTagFromPost(String postId, String tagId);
    
    void unassignAllTagsFromPost(String postId);
    
    boolean exists(String postId, String tagId);
    
    List<PostTag> getAllPaginated();

    PageResponse<PostTag> getAllPaginated(int page, int size);
}
