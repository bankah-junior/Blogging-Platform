package com.amalitech.SpringBootBloggingApp.service.impl;

import com.amalitech.SpringBootBloggingApp.cache.Cache;
import com.amalitech.SpringBootBloggingApp.model.dto.request.CreateCommentRequest;
import com.amalitech.SpringBootBloggingApp.model.dto.response.PageResponse;
import com.amalitech.SpringBootBloggingApp.model.entity.Comment;
import com.amalitech.SpringBootBloggingApp.model.entity.Post;
import com.amalitech.SpringBootBloggingApp.model.entity.User;
import com.amalitech.SpringBootBloggingApp.repository.CommentRepository;
import com.amalitech.SpringBootBloggingApp.repository.PostRepository;
import com.amalitech.SpringBootBloggingApp.repository.UserRepository;
import com.amalitech.SpringBootBloggingApp.service.CommentService;

import com.amalitech.SpringBootBloggingApp.util.ValidationUtil;
import com.amalitech.SpringBootBloggingApp.util.exceptions.UserInputsException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final Cache<String, User> userCache;

    public CommentServiceImpl(CommentRepository commentRepository, UserRepository userRepository, PostRepository postRepository, Cache<String, User> userCache) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.userCache = userCache;
    }

    @Override
    @Transactional
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
    @Transactional
    public Comment create(Comment comment) {
        if(!ValidationUtil.isValidComment(comment.getContent())) {
            throw new UserInputsException("Invalid comment");
        }
        return commentRepository.save(comment);
    }

    @Override
    @Transactional
    public boolean delete(String commentId) {
        if(!ValidationUtil.isValidObjectId(commentId)) {
            throw new UserInputsException("Invalid comment ID");
        }
        commentRepository.deleteById(commentId);
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Comment> getByPost(String postId) {
        if(!ValidationUtil.isValidObjectId(postId)) {
            throw new UserInputsException("Invalid post ID");
        }
        return commentRepository.findByPostId(postId);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<Comment> getByPostPaginated(String postId, int page, int size) {
        if(!ValidationUtil.isValidObjectId(postId)) {
            throw new UserInputsException("Invalid post ID");
        }
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        var pageResult = commentRepository.findByPostId(postId, pageable);
        return new PageResponse<>(pageResult.getContent(), page, size, pageResult.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Comment> getByPost(Post post) {
        return List.of();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Comment> getByUser(String userId) {
        if(!ValidationUtil.isValidObjectId(userId)) {
            throw new UserInputsException("Invalid user ID");
        }
        return commentRepository.findByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<Comment> getByUserPaginated(String userId, int page, int size) {
        if(!ValidationUtil.isValidObjectId(userId)) {
            throw new UserInputsException("Invalid user ID");
        }
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        var pageResult = commentRepository.findByUserId(userId, pageable);
        return new PageResponse<>(pageResult.getContent(), page, size, pageResult.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Comment> getByUser(User user) {
        return List.of();
    }

    @Override
    @Transactional
    public boolean update(Comment comment) {
        if(!ValidationUtil.isValidObjectId(comment.getId())) {
            throw new UserInputsException("Invalid comment ID");
        }
        if(!ValidationUtil.isValidComment(comment.getContent())) {
            throw new UserInputsException("Invalid comment");
        }
        commentRepository.save(comment);
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Comment> getAll() {
        return commentRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<Comment> getAllPaginated(int page, int size) {
        long total = commentRepository.count();
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        var pageResult = commentRepository.findAll(pageable);
        return new PageResponse<>(pageResult.getContent(), page, size, total);
    }

    @Override
    @Transactional(readOnly = true)
    public Comment findById(String commentId) {
        if(!ValidationUtil.isValidObjectId(commentId)) {
            throw new UserInputsException("Invalid comment ID");
        }
         Optional<Comment> comment = commentRepository.findById(commentId);
        return comment.orElse(null);
    }
}
