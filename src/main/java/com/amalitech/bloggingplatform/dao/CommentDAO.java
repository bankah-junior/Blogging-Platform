package com.amalitech.bloggingplatform.dao;

import com.amalitech.bloggingplatform.model.Comment;

import java.util.List;

public interface CommentDAO extends BaseDAO<Comment> {
    List<Comment> findByPostId(String postId);
}

