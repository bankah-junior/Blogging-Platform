package com.amalitech.SpringBootBloggingApp.service.impl;

import com.amalitech.SpringBootBloggingApp.cache.Cache;
import com.amalitech.SpringBootBloggingApp.model.dto.request.CreatePostRequest;
import com.amalitech.SpringBootBloggingApp.model.dto.response.PageResponse;
import com.amalitech.SpringBootBloggingApp.model.entity.Post;
import com.amalitech.SpringBootBloggingApp.model.entity.User;
import com.amalitech.SpringBootBloggingApp.repository.impl.PostRepositoryImpl;
import com.amalitech.SpringBootBloggingApp.repository.impl.UserRepositoryImpl;
import com.amalitech.SpringBootBloggingApp.service.PostService;

import com.amalitech.SpringBootBloggingApp.util.ValidationUtil;
import com.amalitech.SpringBootBloggingApp.util.exceptions.UserInputsException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostServiceImpl implements PostService {
    private final PostRepositoryImpl postRepository;
    private final UserRepositoryImpl userRepository;
    private final Cache<String, User> userCache;

    public PostServiceImpl(PostRepositoryImpl postRepository, UserRepositoryImpl userRepository, Cache<String, User> userCache) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.userCache = userCache;
    }

    @Override
    public Post create(CreatePostRequest request) {
        User author = userRepository.findById(request.getAuthorId()).orElseThrow(() -> new UserInputsException("Author not found"));
        Post post = new Post();
        post.setAuthor(author);
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setPublished(request.getPublished() != null && request.getPublished());
        long now = System.currentTimeMillis();
        post.setCreatedAt(now);
        post.setUpdatedAt(now);
        return postRepository.save(post);
    }

    @Override
    public Post create(Post post) {
        if (!ValidationUtil.isValidTitle(post.getTitle())) {
            throw new UserInputsException("Invalid title");
        }
        if (!ValidationUtil.isValidContent(post.getContent())) {
            throw new UserInputsException("Invalid content");
        }
        return postRepository.save(post);
    }

    @Override
    public Post update(Post post) {
        if (postRepository.update(post)) {
            return post;
        } else {
            return null;
        }
    }

    @Override
    public boolean delete(String postId) {
        if (!ValidationUtil.isValidObjectId(postId)) {
            throw new UserInputsException("Invalid post ID");
        }
        return postRepository.deleteById(postId);
    }

    @Override
    public Post getById(String postId) {
        if (!ValidationUtil.isValidObjectId(postId)) {
            throw new UserInputsException("Invalid post ID");
        }
        return postRepository.findById(postId).orElse(null);
    }

    @Override
    public List<Post> getAll() {
        return postRepository.findAll();
    }

    @Override
    public PageResponse<Post> getAllPaginated(int page, int size) {
        long total = postRepository.count();
        int skip = page * size;
        var content = postRepository.findAll(skip, size);
        return new PageResponse<>(content, page, size, total);
    }

    @Override
    public List<Post> searchByTitle(String keyword) {
        return postRepository.searchByTitle(keyword);
    }

    @Override
    public List<Post> getByAuthor(String authorId) {
        if (!ValidationUtil.isValidObjectId(authorId)) {
            throw new UserInputsException("Invalid author ID");
        }
        return postRepository.findByAuthorId(authorId);
    }

    @Override
    public List<Post> searchByTag(String tagName) {
        return postRepository.findByTagName(tagName);
    }

    @Override
    public List<Post> sortByDate(List<Post> posts, boolean ascending) {
        return posts.stream().sorted((p1, p2) -> {
                    if (ascending) {
                        return p1.getCreatedAt().compareTo(p2.getCreatedAt());
                    } else {
                        return p2.getCreatedAt().compareTo(p1.getCreatedAt());
                    }
                })
                .toList();
    }

    @Override
    public List<Post> sortByTitle(List<Post> posts, boolean ascending) {
        return posts.stream().sorted((p1, p2) -> {
                    if (ascending) {
                        return p1.getTitle().compareToIgnoreCase(p2.getTitle());
                    } else {
                        return p2.getTitle().compareToIgnoreCase(p1.getTitle());
                    }
                })
                .toList();
    }

    @Override
    public List<Post> getByAuthor(com.amalitech.SpringBootBloggingApp.model.entity.User author) {
        return postRepository.findByAuthor(author);
    }

    @Override
    public List<Post> getByPublished(boolean published) {
        return postRepository.findByPublished(published);
    }

    @Override
    public List<Post> getAllSorted(String sortBy, boolean ascending) {
        return switch (sortBy.toLowerCase()) {
            case "date" -> sortByDate(getAll(), ascending);
            case "title" -> sortByTitle(getAll(), ascending);
            default -> getAll();
        };
    }
}
