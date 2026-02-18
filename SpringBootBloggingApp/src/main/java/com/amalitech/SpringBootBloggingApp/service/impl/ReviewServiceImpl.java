package com.amalitech.SpringBootBloggingApp.service.impl;

import com.amalitech.SpringBootBloggingApp.cache.Cache;
import com.amalitech.SpringBootBloggingApp.model.dto.request.CreateReviewRequest;
import com.amalitech.SpringBootBloggingApp.model.dto.response.PageResponse;
import com.amalitech.SpringBootBloggingApp.model.entity.Post;
import com.amalitech.SpringBootBloggingApp.model.entity.Review;
import com.amalitech.SpringBootBloggingApp.model.entity.User;
import com.amalitech.SpringBootBloggingApp.repository.PostRepository;
import com.amalitech.SpringBootBloggingApp.repository.ReviewRepository;
import com.amalitech.SpringBootBloggingApp.repository.UserRepository;
import com.amalitech.SpringBootBloggingApp.service.ReviewService;
import com.amalitech.SpringBootBloggingApp.util.ValidationUtil;
import com.amalitech.SpringBootBloggingApp.util.exceptions.UserInputsException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final Cache<String, User> userCache;

    public ReviewServiceImpl(ReviewRepository reviewRepository, UserRepository userRepository, PostRepository postRepository, Cache<String, User> userCache) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.userCache = userCache;
    }

    @Override
    @Transactional
    public Review create(CreateReviewRequest request) {
        User user = userRepository.findById(request.getUserId()).orElseThrow(() -> new UserInputsException("User not found"));
        Post post = postRepository.findById(request.getPostId()).orElseThrow(() -> new UserInputsException("Post not found"));
        Review review = new Review();
        review.setPost(post);
        review.setUser(user);
        review.setRating(request.getRating());
        review.setFeedback(request.getFeedback() != null ? request.getFeedback() : "");
        long now = System.currentTimeMillis();
        review.setCreatedAt(now);
        review.setUpdatedAt(now);
        return reviewRepository.save(review);
    }

    @Override
    @Transactional
    @CacheEvict(value = "reviews", allEntries = true)
    public Review create(Review review) {
        if (!ValidationUtil.isValidObjectId(review.getUser().getId())) {
            throw new UserInputsException("Invalid user ID");
        }
        if (!ValidationUtil.isValidObjectId(review.getPost().getId())) {
            throw new UserInputsException("Invalid post ID");
        }
        if (!ValidationUtil.isValidRating(review.getRating())) {
            throw new UserInputsException("Invalid rating");
        }
        if (!ValidationUtil.isValidContent(review.getFeedback())) {
            throw new UserInputsException("Invalid content");
        }
        return reviewRepository.save(review);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "reviews", key = "#reviewId")
    public Review getById(String reviewId) {
        if (!ValidationUtil.isValidObjectId(reviewId)) {
            throw new UserInputsException("Invalid review ID");
        }
        return reviewRepository.findById(reviewId).orElse(null);
    }

    @Override
    @Transactional
    @CacheEvict(value = "reviews", allEntries = true)
    public boolean delete(String reviewId) {
        if (!ValidationUtil.isValidObjectId(reviewId)) {
            throw new UserInputsException("Invalid review ID");
        }
        reviewRepository.deleteById(reviewId);
        return true;
    }

    @Override
    @Transactional
    @CacheEvict(value = "reviews", allEntries = true)
    public boolean update(Review review) {
        if (!ValidationUtil.isValidObjectId(review.getId())) {
            throw new UserInputsException("Invalid review ID");
        }
        reviewRepository.save(review);
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "reviews", key = "#postId")
    public List<Review> getByPost(String postId) {
        if (!ValidationUtil.isValidObjectId(postId)) {
            throw new UserInputsException("Invalid post ID");
        }
        return reviewRepository.findByPostId(postId);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<Review> getByPostPaginated(String postId, int page, int size) {
        if (!ValidationUtil.isValidObjectId(postId)) {
            throw new UserInputsException("Invalid post ID");
        }
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        var pageResult = reviewRepository.findByPostId(postId, pageable);
        return new PageResponse<>(pageResult.getContent(), page, size, pageResult.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Review> getByPost(Post post) {
        return List.of();
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "reviews", key = "#userId")
    public List<Review> getByUser(String userId) {
        if (!ValidationUtil.isValidObjectId(userId)) {
            throw new UserInputsException("Invalid user ID");
        }
        return reviewRepository.findByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<Review> getByUserPaginated(String userId, int page, int size) {
        if (!ValidationUtil.isValidObjectId(userId)) {
            throw new UserInputsException("Invalid user ID");
        }
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        var pageResult = reviewRepository.findByUserId(userId, pageable);
        return new PageResponse<>(pageResult.getContent(), page, size, pageResult.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Review> getByUser(User user) {
        return List.of();
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "reviews", key = "#postId")
    public double getAverageRatingForPost(String postId) {
        if (!ValidationUtil.isValidObjectId(postId)) {
            throw new UserInputsException("Invalid post ID");
        }
        Double avgRating = reviewRepository.calculateAverageRatingByPostId(postId);
        return avgRating != null ? avgRating : 0.0;
    }

    @Override
    @Transactional(readOnly = true)
    public double getAverageRatingForPost(Post post) {
        return 0;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "reviews", key = "'all'")
    public List<Review> getAll() {
        return reviewRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<Review> getAllPaginated(int page, int size) {
        long total = reviewRepository.count();
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        var pageResult = reviewRepository.findAll(pageable);
        return new PageResponse<>(pageResult.getContent(), page, size, total);
    }
}
