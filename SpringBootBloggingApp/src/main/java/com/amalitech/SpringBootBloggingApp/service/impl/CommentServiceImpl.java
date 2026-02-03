package com.amalitech.SpringBootBloggingApp.service.impl;

import com.amalitech.SpringBootBloggingApp.cache.Cache;
import com.amalitech.SpringBootBloggingApp.model.dto.request.CreateCommentRequest;
import com.amalitech.SpringBootBloggingApp.model.dto.response.PageResponse;
import com.amalitech.SpringBootBloggingApp.model.entity.Comment;
import com.amalitech.SpringBootBloggingApp.model.entity.Post;
import com.amalitech.SpringBootBloggingApp.model.entity.User;
import com.amalitech.SpringBootBloggingApp.repository.impl.CommentRepositoryImpl;
import com.amalitech.SpringBootBloggingApp.repository.impl.PostRepositoryImpl;
import com.amalitech.SpringBootBloggingApp.repository.impl.UserRepositoryImpl;
import com.amalitech.SpringBootBloggingApp.service.CommentService;

import com.amalitech.SpringBootBloggingApp.util.ValidationUtil;
import com.amalitech.SpringBootBloggingApp.util.exceptions.UserInputsException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommentServiceImpl implements CommentService {
    private final CommentRepositoryImpl commentRepository;
    private final UserRepositoryImpl userRepository;
    private final PostRepositoryImpl postRepository;
    private final Cache<String, User> userCache;

    public CommentServiceImpl(CommentRepositoryImpl commentRepository, UserRepositoryImpl userRepository, PostRepositoryImpl postRepository, Cache<String, User> userCache) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.userCache = userCache;
    }

    @Override
    public Comment create(CreateCommentRequest request) {
        User user = userRepository.findById(request.getUserId()).orElseThrow(() -> new UserInputsException("User not found"));
        Post post = postRepository.findById(request.getPostId()).orElseThrow(() -> new UserInputsException("Post not found"));
        Comment comment = new Comment();
        comment.setPost(post);
        comment.setUser(user);
        comment.setContent(request.getContent());
        long now = System.currentTimeMillis();
        comment.setCreatedAt(now);
        comment.setUpdatedAt(now);
        return commentRepository.save(comment);
    }

    @Override
    public Comment create(Comment comment) {
        if(!ValidationUtil.isValidComment(comment.getContent())) {
            throw new UserInputsException("Invalid comment");
        }
        return commentRepository.save(comment);
    }

    @Override
    public boolean delete(String commentId) {
        if(!ValidationUtil.isValidObjectId(commentId)) {
            throw new UserInputsException("Invalid comment ID");
        }
        return commentRepository.deleteById(commentId);
    }

    @Override
    public List<Comment> getByPost(String postId) {
        if(!ValidationUtil.isValidObjectId(postId)) {
            throw new UserInputsException("Invalid post ID");
        }
        return commentRepository.findByPostId(postId);
    }

    @Override
    public List<Comment> getByPost(Post post) {
        return List.of();
    }

    @Override
    public List<Comment> getByUser(String userId) {
        if(!ValidationUtil.isValidObjectId(userId)) {
            throw new UserInputsException("Invalid user ID");
        }
        return commentRepository.findByUserId(userId);
    }

    @Override
    public List<Comment> getByUser(User user) {
        return List.of();
    }

    @Override
    public boolean update(Comment comment) {
        if(!ValidationUtil.isValidObjectId(comment.getId())) {
            throw new UserInputsException("Invalid comment ID");
        }
        if(!ValidationUtil.isValidComment(comment.getContent())) {
            throw new UserInputsException("Invalid comment");
        }
        return commentRepository.update(comment);
    }

    @Override
    public List<Comment> getAll() {
        return commentRepository.findAll();
    }

    @Override
    public PageResponse<Comment> getAllPaginated(int page, int size) {
        long total = commentRepository.count();
        int skip = page * size;
        var content = commentRepository.findAll(skip, size);
        return new PageResponse<>(content, page, size, total);
    }

    @Override
    public Comment findById(String commentId) {
        if(!ValidationUtil.isValidObjectId(commentId)) {
            throw new UserInputsException("Invalid comment ID");
        }
         Optional<Comment> comment = commentRepository.findById(commentId);
        return comment.orElse(null);
    }
}
