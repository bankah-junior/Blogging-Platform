package com.amalitech.bloggingplatform.service;

import com.amalitech.bloggingplatform.model.Post;

import java.util.List;

public interface PostService {

    Post create(Post post);

    Post update(Post post);

    boolean delete(String postId);

    Post getById(String postId);

    List<Post> getAll();

    List<Post> searchByTitle(String keyword);

    List<Post> getByAuthor(String authorId);
    
    List<Post> searchByTag(String tagName);
    
    List<Post> sortByDate(List<Post> posts, boolean ascending);
    
    List<Post> sortByTitle(List<Post> posts, boolean ascending);
    
    List<Post> getAllSorted(String sortBy, boolean ascending);
}

