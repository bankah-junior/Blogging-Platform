package com.amalitech.bloggingplatform.dao;

import com.amalitech.bloggingplatform.model.Post;

import java.util.List;

public interface PostDAO extends BaseDAO<Post> {

    List<Post> findByAuthorId(String authorId);

    List<Post> searchByTitle(String keyword);
}

