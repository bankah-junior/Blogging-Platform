package com.amalitech.SpringBootBloggingApp.service;

import com.amalitech.SpringBootBloggingApp.model.dto.request.CreatePostRequest;
import com.amalitech.SpringBootBloggingApp.model.dto.response.PageResponse;
import com.amalitech.SpringBootBloggingApp.model.entity.Post;

import java.util.List;

public interface PostService {

    Post create(Post post);

    Post create(CreatePostRequest request);

    Post update(Post post);

    boolean delete(String postId);

    Post getById(String postId);

    List<Post> getAll();

    PageResponse<Post> getAllPaginated(int page, int size);

    List<Post> searchByTitle(String keyword);

    List<Post> getByAuthor(String authorId);
    
    List<Post> getByAuthor(com.amalitech.SpringBootBloggingApp.model.entity.User author);

    List<Post> searchByTag(String tagName);
    
    List<Post> getByPublished(boolean published);

    List<Post> sortByDate(List<Post> posts, boolean ascending);

    List<Post> sortByTitle(List<Post> posts, boolean ascending);

    List<Post> getAllSorted(String sortBy, boolean ascending);
}
