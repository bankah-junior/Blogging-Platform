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
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

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
    public Review getById(String reviewId) {
        if (!ValidationUtil.isValidObjectId(reviewId)) {
            throw new UserInputsException("Invalid review ID");
        }
        return reviewRepository.findById(reviewId).orElse(null);
    }

    @Override
    public boolean delete(String reviewId) {
        if (!ValidationUtil.isValidObjectId(reviewId)) {
            throw new UserInputsException("Invalid review ID");
        }
        reviewRepository.deleteById(reviewId);
        return true;
    }

    @Override
    public boolean update(Review review) {
        if (!ValidationUtil.isValidObjectId(review.getId())) {
            throw new UserInputsException("Invalid review ID");
        }
        reviewRepository.save(review);
        return true;
    }

    @Override
    public List<Review> getByPost(String postId) {
        if (!ValidationUtil.isValidObjectId(postId)) {
            throw new UserInputsException("Invalid post ID");
        }
        return reviewRepository.findByPostId(postId);
    }

    @Override
    public List<Review> getByPost(Post post) {
        return List.of();
    }

    @Override
    public List<Review> getByUser(String userId) {
        return List.of();
    }

    @Override
    public List<Review> getByUser(User user) {
        return List.of();
    }

    @Override
    public double getAverageRatingForPost(String postId) {
        if (!ValidationUtil.isValidObjectId(postId)) {
            throw new UserInputsException("Invalid post ID");
        }
        Double avgRating = reviewRepository.calculateAverageRatingByPostId(postId);
        return avgRating != null ? avgRating : 0.0;
    }

    @Override
    public double getAverageRatingForPost(Post post) {
        return 0;
    }

    @Override
    public List<Review> getAll() {
        return reviewRepository.findAll();
    }

    @Override
    public PageResponse<Review> getAllPaginated(int page, int size) {
        long total = reviewRepository.count();
        var pageable = PageRequest.of(page, size);
        var pageResult = reviewRepository.findAll(pageable);
        return new PageResponse<>(pageResult.getContent(), page, size, total);
    }
}
