package com.amalitech.SpringBootBloggingApp.service.impl;

import com.amalitech.SpringBootBloggingApp.cache.Cache;
import com.amalitech.SpringBootBloggingApp.model.dto.request.CreatePostRequest;
import com.amalitech.SpringBootBloggingApp.model.dto.response.PageResponse;
import com.amalitech.SpringBootBloggingApp.model.entity.Post;
import com.amalitech.SpringBootBloggingApp.model.entity.User;
import com.amalitech.SpringBootBloggingApp.repository.PostRepository;
import com.amalitech.SpringBootBloggingApp.repository.UserRepository;
import com.amalitech.SpringBootBloggingApp.service.PostService;

import com.amalitech.SpringBootBloggingApp.util.ValidationUtil;
import com.amalitech.SpringBootBloggingApp.util.exceptions.UserInputsException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final Cache<String, User> userCache;

    public PostServiceImpl(PostRepository postRepository, UserRepository userRepository, Cache<String, User> userCache) {
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
        Post saved = postRepository.save(post);
        return saved;
    }

    @Override
    public boolean delete(String postId) {
        if (!ValidationUtil.isValidObjectId(postId)) {
            throw new UserInputsException("Invalid post ID");
        }
        postRepository.deleteById(postId);
        return true;
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
        var pageable = PageRequest.of(page, size);
        var pageResult = postRepository.findAll(pageable);
        return new PageResponse<>(pageResult.getContent(), page, size, total);
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
    public PageResponse<Post> getByAuthorPaginated(String authorId, int page, int size) {
        if (!ValidationUtil.isValidObjectId(authorId)) {
            throw new UserInputsException("Invalid author ID");
        }
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        var pageResult = postRepository.findByAuthorId(authorId, pageable);
        return new PageResponse<>(pageResult.getContent(), page, size, pageResult.getTotalElements());
    }

    @Override
    public List<Post> searchByTag(String tagName) {
        // This method requires joining through PostTag
        // For now, return empty list - will be implemented with proper aggregation
        return List.of();
    }

    @Override
    public PageResponse<Post> searchByTagPaginated(String tagName, int page, int size) {
        // This method requires joining through PostTag
        // For now, return empty page - will be implemented with proper aggregation
        return new PageResponse<>(List.of(), page, size, 0L);
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
    public PageResponse<Post> getByPublishedPaginated(boolean published, int page, int size) {
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        var pageResult = postRepository.findByPublished(published, pageable);
        return new PageResponse<>(pageResult.getContent(), page, size, pageResult.getTotalElements());
    }

    @Override
    public List<Post> getAllSorted(String sortBy, boolean ascending) {
        return switch (sortBy.toLowerCase()) {
            case "date" -> sortByDate(getAll(), ascending);
            case "title" -> sortByTitle(getAll(), ascending);
            default -> getAll();
        };
    }

    @Override
    public PageResponse<Post> getAllSortedPaginated(String sortBy, boolean ascending, int page, int size) {
        Sort.Direction direction = ascending ? Sort.Direction.ASC : Sort.Direction.DESC;
        String sortField = sortBy.equalsIgnoreCase("date") ? "createdAt" : "title";
        var pageable = PageRequest.of(page, size, Sort.by(direction, sortField));
        var pageResult = postRepository.findAll(pageable);
        return new PageResponse<>(pageResult.getContent(), page, size, pageResult.getTotalElements());
    }
}
