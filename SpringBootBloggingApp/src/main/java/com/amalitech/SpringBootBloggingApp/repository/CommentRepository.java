package com.amalitech.SpringBootBloggingApp.repository;

import com.amalitech.SpringBootBloggingApp.model.entity.Comment;

import java.util.List;

public interface CommentRepository extends BaseRepository<Comment> {
    List<Comment> findByPostId(String postId);
    List<Comment> findByPost(com.amalitech.SpringBootBloggingApp.model.entity.Post post);
    List<Comment> findByUserId(String userId);
    List<Comment> findByUser(com.amalitech.SpringBootBloggingApp.model.entity.User user);
}
