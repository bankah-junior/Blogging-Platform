package com.amalitech.bloggingplatform.service;

import com.amalitech.bloggingplatform.model.entity.Comment;

import java.util.List;

public interface CommentService {

    Comment create(Comment comment);

    boolean delete(String commentId);

    List<Comment> getByPost(String postId);

    List<Comment> getByUser(String userId);
}

