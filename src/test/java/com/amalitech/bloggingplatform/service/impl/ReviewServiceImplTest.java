package com.amalitech.bloggingplatform.service.impl;

import com.amalitech.bloggingplatform.dao.ReviewDAO;
import com.amalitech.bloggingplatform.model.Review;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    @Mock
    private ReviewDAO reviewDAO;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private Review review;

    @BeforeEach
    void setUp() {
        review = new Review();
        review.setId("1");
        review.setPostId("postId");
        review.setUserId("userId");
        review.setRating(5);
        review.setFeedback("Great post!");
    }

    @Test
    void create() {
        when(reviewDAO.save(any(Review.class))).thenReturn(review);

        Review createdReview = reviewService.create(review);

        assertNotNull(createdReview);
        assertEquals(review.getId(), createdReview.getId());
        verify(reviewDAO, times(1)).save(review);
    }

    @Test
    void delete() {
        when(reviewDAO.deleteById(anyString())).thenReturn(true);

        boolean deleted = reviewService.delete("1");

        assertTrue(deleted);
        verify(reviewDAO, times(1)).deleteById("1");
    }

    @Test
    void getByPost() {
        when(reviewDAO.findByPostId(anyString())).thenReturn(Collections.singletonList(review));

        List<Review> reviews = reviewService.getByPost("postId");

        assertNotNull(reviews);
        assertEquals(1, reviews.size());
        verify(reviewDAO, times(1)).findByPostId("postId");
    }

    @Test
    void getAverageRatingForPost() {
        when(reviewDAO.calculateAverageRating(anyString())).thenReturn(4.5);

        double averageRating = reviewService.getAverageRatingForPost("postId");

        assertEquals(4.5, averageRating);
        verify(reviewDAO, times(1)).calculateAverageRating("postId");
    }
}
