package com.amalitech.SpringBootBloggingApp.repository;

import com.amalitech.SpringBootBloggingApp.model.entity.PostTag;

import java.util.List;

public interface PostTagRepository extends BaseRepository<PostTag> {
    
    List<PostTag> findByPostId(String postId);
    
    List<PostTag> findByTagId(String tagId);
    
    void deleteByPostId(String postId);
    
    void deleteByTagId(String tagId);
    
    boolean existsByPostIdAndTagId(String postId, String tagId);
}
