package com.amalitech.SpringBootBloggingApp.controller;

import com.amalitech.SpringBootBloggingApp.model.dto.DtoMapper;
import com.amalitech.SpringBootBloggingApp.model.dto.request.CreateReviewRequest;
import com.amalitech.SpringBootBloggingApp.model.dto.request.UpdateReviewRequest;
import com.amalitech.SpringBootBloggingApp.model.dto.response.ApiResponse;
import com.amalitech.SpringBootBloggingApp.model.dto.response.PageResponse;
import com.amalitech.SpringBootBloggingApp.model.dto.response.ReviewResponse;
import com.amalitech.SpringBootBloggingApp.model.entity.Review;
import com.amalitech.SpringBootBloggingApp.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reviews")
@ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Bad request"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Resource not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
})
public class ReviewController {
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/create")
    @Operation(summary = "Create a new review", description = "Creates a new review for a post")
    @SecurityRequirement(name = "Bearer Authentication")
    @Tag(name = "Review")
    public ResponseEntity<ApiResponse<ReviewResponse>> create(@Valid @RequestBody CreateReviewRequest request) {
        Review createdReview = reviewService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Review created", DtoMapper.toReviewResponse(createdReview)));
    }

    @DeleteMapping("/delete/{reviewId}")
    @Operation(summary = "Delete a review by ID", description = "Deletes a review by its ID")
    @SecurityRequirement(name = "Bearer Authentication")
    @Tag(name = "Review")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String reviewId) {
        boolean isDeleted = reviewService.delete(reviewId);
        if (!isDeleted) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Review not found"));
        return ResponseEntity.ok(ApiResponse.success("Review deleted", null));
    }

    @PutMapping("/update")
    @Operation(summary = "Update a review", description = "Updates a review")
    @SecurityRequirement(name = "Bearer Authentication")
    @Tag(name = "Review")
    public ResponseEntity<ApiResponse<ReviewResponse>> update(@Valid @RequestBody UpdateReviewRequest request) {
        Review existing = reviewService.getById(request.getId());
        if (existing == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Review not found"));
        existing.setRating(request.getRating());
        existing.setFeedback(request.getFeedback() != null ? request.getFeedback() : "");
        existing.setUpdatedAt(System.currentTimeMillis());
        reviewService.update(existing);
        return ResponseEntity.ok(ApiResponse.success("Review updated", DtoMapper.toReviewResponse(existing)));
    }

    @GetMapping
    @Operation(summary = "Get all reviews", description = "Retrieves reviews with optional pagination (page, size)")
    @Tag(name = "Review")
    public ResponseEntity<ApiResponse<PageResponse<ReviewResponse>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var pr = reviewService.getAllPaginated(page, size);
        var dto = new PageResponse<>(DtoMapper.toReviewResponses(pr.getContent()), pr.getPage(), pr.getSize(), pr.getTotalElements());
        return ResponseEntity.ok(ApiResponse.success(dto));
    }

    @GetMapping("/post/{postId}")
    @Operation(summary = "Get reviews by post ID", description = "Retrieves reviews for a specific post with optional pagination")
    @Tag(name = "Review")
    public ResponseEntity<ApiResponse<?>> getByPost(
            @PathVariable String postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageResponse<Review> pr = reviewService.getByPostPaginated(postId, page, size);
        PageResponse<ReviewResponse> dto = new PageResponse<>(
                DtoMapper.toReviewResponses(pr.getContent()), pr.getPage(), pr.getSize(), pr.getTotalElements());
        return ResponseEntity.ok(ApiResponse.success(dto));
    }

    @GetMapping("/post/{postId}/average-rating")
    @Operation(summary = "Get average rating for a post", description = "Retrieves the average rating for a specific post")
    @Tag(name = "Review")
    public ResponseEntity<ApiResponse<Double>> getAverageRatingForPost(@PathVariable String postId) {
        double averageRating = reviewService.getAverageRatingForPost(postId);
        return ResponseEntity.ok(ApiResponse.success(averageRating));
    }
}
