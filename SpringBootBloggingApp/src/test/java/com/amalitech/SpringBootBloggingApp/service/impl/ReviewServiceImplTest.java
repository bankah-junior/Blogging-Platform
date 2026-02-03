package com.amalitech.SpringBootBloggingApp.service.impl;

import com.amalitech.SpringBootBloggingApp.cache.Cache;
import com.amalitech.SpringBootBloggingApp.model.entity.Post;
import com.amalitech.SpringBootBloggingApp.model.entity.Review;
import com.amalitech.SpringBootBloggingApp.model.entity.User;
import com.amalitech.SpringBootBloggingApp.repository.impl.ReviewRepositoryImpl;
import com.amalitech.SpringBootBloggingApp.util.exceptions.UserInputsException;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    @Mock
    private ReviewRepositoryImpl reviewRepository;

    @Mock
    private Cache<String, User> userCache;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private Review testReview;
    private Review testReview2;
    private List<Review> testReviews;

    private Post testPost;
    private Post testPost2;

    private User testUser;
    private User testUser2;

    @BeforeEach
    void setUp() {
        testUser = new User("697349d17b196ad927aa89fa", "testuser", "testuser@example.com", "password123", 123456789L, null);
        testUser2 = new User("697349d17b196ad927aa89fb", "testuser2", "testuser2@example.com", "password456", 123456790L, null);

        testPost = new Post("697349d17b196ad927aa89fc", testUser, "Test Post", "This is a test post", true, 123456789L, null, List.of());
        testPost2 = new Post("697349d17b196ad927aa89fd", testUser2, "Test Post 2", "This is another test post", true, 123456790L, null, List.of());

        testReview = new Review("6972347565b2f32d5ed11f0d", testPost, testUser, 5, "Excellent post!", 123456789L, null);
        testReview2 = new Review("6972347565b2f32d5ed11f0e", testPost2, testUser2, 4, "Good content", 123456790L, null);
        testReviews = List.of(testReview, testReview2);
    }

    @Test
    @DisplayName("Create Valid Review Returns Review")
    void create_ValidReview_ReturnsReview() {
        when(reviewRepository.save(any(Review.class))).thenReturn(testReview);

        Review result = reviewService.create(testReview);

        assertNotNull(result);
        assertEquals(testReview.getId(), result.getId());
        assertEquals(testReview.getUser().getId(), result.getUser().getId());
        assertEquals(testReview.getPost().getId(), result.getPost().getId());
        assertEquals(testReview.getRating(), result.getRating());
        assertEquals(testReview.getFeedback(), result.getFeedback());
        verify(reviewRepository).save(testReview);
    }

    @Test
    @DisplayName("Create Invalid UserId Throws UserInputsException")
    void create_InvalidUserId_ThrowsUserInputsException() {
        User invalidUser = new User("1", "invaliduser", "invaliduser@example.com", "password123", 123456789L, null);
        Review invalidReview = new Review("1", testPost, invalidUser, 5, "Excellent post!", 123456789L, null);

        assertThrows(UserInputsException.class, () -> reviewService.create(invalidReview));
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    @DisplayName("Create Invalid PostId Throws UserInputsException")
    void create_InvalidPostId_ThrowsUserInputsException() {
        testPost.setId("1");
        Review invalidReview = new Review("1", testPost, testUser, 5, "Excellent post!", 123456789L, null);

        assertThrows(UserInputsException.class, () -> reviewService.create(invalidReview));
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    @DisplayName("Create Invalid Rating Throws UserInputsException")
    void create_InvalidRating_ThrowsUserInputsException() {
        Review invalidReview = new Review("1", testPost, testUser, 6, "Excellent post!", 123456789L, null);

        assertThrows(UserInputsException.class, () -> reviewService.create(invalidReview));
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    @DisplayName("Create Invalid Rating Zero Throws UserInputsException")
    void create_InvalidRatingZero_ThrowsUserInputsException() {
        Review invalidReview = new Review("1", testPost, testUser, 0, "Excellent post!", 123456789L, null);

        assertThrows(UserInputsException.class, () -> reviewService.create(invalidReview));
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    @DisplayName("Create Invalid Feedback Throws UserInputsException")
    void create_InvalidFeedback_ThrowsUserInputsException() {
        Review invalidReview = new Review("1", testPost, testUser, 5, "", 123456789L, null);

        assertThrows(UserInputsException.class, () -> reviewService.create(invalidReview));
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    @DisplayName("Create Null Feedback Throws UserInputsException")
    void create_NullFeedback_ThrowsUserInputsException() {
        Review invalidReview = new Review("1", testPost, testUser, 5, null, 123456789L, null);

        assertThrows(UserInputsException.class, () -> reviewService.create(invalidReview));
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    @DisplayName("Delete Valid Review Id Returns True")
    void delete_ValidReviewId_ReturnsTrue() {
        when(reviewRepository.deleteById(testReview.getId())).thenReturn(true);

        boolean result = reviewService.delete(testReview.getId());

        assertTrue(result);
        verify(reviewRepository).deleteById(testReview.getId());
    }

    @Test
    @DisplayName("Delete Invalid Review Id Throws UserInputsException")
    void delete_InvalidReviewId_ThrowsUserInputsException() {
        assertThrows(UserInputsException.class, () -> reviewService.delete("invalid-id"));
        verify(reviewRepository, never()).deleteById(anyString());
    }

    @Test
    @DisplayName("Delete Failed Delete Returns False")
    void delete_FailedDelete_ReturnsFalse() {
        when(reviewRepository.deleteById(testReview.getId())).thenReturn(false);

        boolean result = reviewService.delete(testReview.getId());

        assertFalse(result);
        verify(reviewRepository).deleteById(testReview.getId());
    }

    @Test
    @DisplayName("Update Valid Review Returns True")
    void update_ValidReview_ReturnsTrue() {
        when(reviewRepository.update(testReview)).thenReturn(true);

        boolean result = reviewService.update(testReview);

        assertTrue(result);
        verify(reviewRepository).update(testReview);
    }

    @Test
    @DisplayName("Update Invalid Review Id Throws UserInputsException")
    void update_InvalidReviewId_ThrowsUserInputsException() {
        Review invalidReview = new Review("invalid-id", testPost, testUser, 5, "Excellent post!", 123456789L, null);

        assertThrows(UserInputsException.class, () -> reviewService.update(invalidReview));
        verify(reviewRepository, never()).update(any(Review.class));
    }

    @Test
    @DisplayName("Update Failed Update Returns False")
    void update_FailedUpdate_ReturnsFalse() {
        when(reviewRepository.update(testReview)).thenReturn(false);

        boolean result = reviewService.update(testReview);

        assertFalse(result);
        verify(reviewRepository).update(testReview);
    }

    @Test
    @DisplayName("Get Reviews By Post Valid Post Id Returns Post Reviews")
    void getByPost_ValidPostId_ReturnsPostReviews() {
        List<Review> expectedReviews = List.of(testReview, testReview2);
        when(reviewRepository.findByPostId(testPost2.getId())).thenReturn(expectedReviews);

        List<Review> result = reviewService.getByPost(testPost2.getId());

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(testReview.getPost().getId(), result.get(0).getPost().getId());
        assertEquals(testReview2.getPost().getId(), result.get(1).getPost().getId());
        verify(reviewRepository).findByPostId(testPost2.getId());
    }

    @Test
    @DisplayName("Get Reviews By Post Invalid Post Id Throws UserInputsException")
    void getByPost_InvalidPostId_ThrowsUserInputsException() {
        assertThrows(UserInputsException.class, () -> reviewService.getByPost("invalid-id"));
        verify(reviewRepository, never()).findByPostId(anyString());
    }

    @Test
    @DisplayName("Get Reviews By Post No Reviews Returns Empty List")
    void getByPost_NoReviews_ReturnsEmptyList() {
        when(reviewRepository.findByPostId("696e40248e370aa034f5f28a")).thenReturn(List.of());

        List<Review> result = reviewService.getByPost("696e40248e370aa034f5f28a");

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(reviewRepository).findByPostId("696e40248e370aa034f5f28a");
    }

    @Test
    @DisplayName("Get Average Rating For Post Valid Post Id Returns Average Rating")
    void getAverageRatingForPost_ValidPostId_ReturnsAverageRating() {
        when(reviewRepository.calculateAverageRating(testPost2.getId())).thenReturn(4.5);

        double result = reviewService.getAverageRatingForPost(testPost2.getId());

        assertEquals(4.5, result);
        verify(reviewRepository).calculateAverageRating(testPost2.getId());
    }

    @Test
    @DisplayName("Get Average Rating For Post Invalid Post Id Throws UserInputsException")
    void getAverageRatingForPost_InvalidPostId_ThrowsUserInputsException() {
        assertThrows(UserInputsException.class, () -> reviewService.getAverageRatingForPost("invalid-id"));
        verify(reviewRepository, never()).calculateAverageRating(anyString());
    }

    @Test
    @DisplayName("Get Average Rating For Post No Reviews Returns Zero")
    void getAverageRatingForPost_NoReviews_ReturnsZero() {
        when(reviewRepository.calculateAverageRating("696e40248e370aa034f5f28a")).thenReturn(0.0);

        double result = reviewService.getAverageRatingForPost("696e40248e370aa034f5f28a");

        assertEquals(0.0, result);
        verify(reviewRepository).calculateAverageRating("696e40248e370aa034f5f28a");
    }

    @Test
    @DisplayName("Get All Reviews Returns List Of Reviews")
    void getAll_ReturnsListOfReviews() {
        when(reviewRepository.findAll()).thenReturn(testReviews);

        List<Review> result = reviewService.getAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(testReview.getId(), result.get(0).getId());
        assertEquals(testReview2.getId(), result.get(1).getId());
        verify(reviewRepository).findAll();
    }

    @Test
    @DisplayName("Get All Reviews Empty List Returns Empty List")
    void getAll_EmptyList_ReturnsEmptyList() {
        when(reviewRepository.findAll()).thenReturn(List.of());

        List<Review> result = reviewService.getAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(reviewRepository).findAll();
    }

    @Test
    @DisplayName("Create Review With Valid Data Calls Repository Save")
    void create_ReviewWithValidData_CallsRepositorySave() {
        Review validReview = new Review("3", testPost, testUser, 3, "Average content", 123456791L, null);
        when(reviewRepository.save(validReview)).thenReturn(validReview);

        Review result = reviewService.create(validReview);

        assertNotNull(result);
        verify(reviewRepository).save(validReview);
    }

    @Test
    @DisplayName("Create Review With Minimum Rating Returns Review")
    void create_ReviewWithMinimumRating_ReturnsReview() {
        Review validReview = new Review("4", testPost, testUser, 1, "Poor content", 123456792L, null);
        when(reviewRepository.save(validReview)).thenReturn(validReview);

        Review result = reviewService.create(validReview);

        assertNotNull(result);
        assertEquals(1, result.getRating());
        verify(reviewRepository).save(validReview);
    }

    @Test
    @DisplayName("Create Review With Maximum Rating Returns Review")
    void create_ReviewWithMaximumRating_ReturnsReview() {
        Review validReview = new Review("6972347565b2f32d5ed11f0f", testPost, testUser, 5, "Excellent content", 123456793L, null);
        when(reviewRepository.save(validReview)).thenReturn(validReview);

        Review result = reviewService.create(validReview);

        assertNotNull(result);
        assertEquals(5, result.getRating());
        verify(reviewRepository).save(validReview);
    }

    @Test
    @DisplayName("Update Review With Valid Id Calls Repository Update")
    void update_ReviewWithValidId_CallsRepositoryUpdate() {
        Review updatedReview = new Review(testReview.getId(), testPost, testUser, 4, "Updated feedback", 123456794L, null);
        when(reviewRepository.update(updatedReview)).thenReturn(true);

        boolean result = reviewService.update(updatedReview);

        assertTrue(result);
        verify(reviewRepository).update(updatedReview);
    }

    @Test
    @DisplayName("Delete Review With Valid Id Calls Repository DeleteById")
    void delete_ReviewWithValidId_CallsRepositoryDeleteById() {
        when(reviewRepository.deleteById(testReview.getId())).thenReturn(true);

        boolean result = reviewService.delete(testReview.getId());

        assertTrue(result);
        verify(reviewRepository).deleteById(testReview.getId());
    }

    @Test
    @DisplayName("Get Reviews By Post Id Returns List Of Reviews")
    void getByPost_ReviewWithValidPostId_CallsRepositoryFindByPostId() {
        when(reviewRepository.findByPostId(testPost.getId())).thenReturn(List.of(testReview));

        List<Review> result = reviewService.getByPost(testPost.getId());
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(reviewRepository).findByPostId(testPost.getId());
    }

    @Test
    @DisplayName("Get Average Rating For Post With Valid Post Id Calls Repository CalculateAverageRating")
    void getAverageRatingForPost_ReviewWithValidPostId_CallsRepositoryCalculateAverageRating() {
        when(reviewRepository.calculateAverageRating(testPost.getId())).thenReturn(3.5);

        double result = reviewService.getAverageRatingForPost(testPost.getId());

        assertEquals(3.5, result);
        verify(reviewRepository).calculateAverageRating(testPost.getId());
    }

    @Test
    @DisplayName("Create Review With Negative Rating Throws UserInputsException")
    void create_ReviewWithNegativeRating_ThrowsUserInputsException() {
        Review invalidReview = new Review("1", testPost, testUser, -1, "Negative rating", 123456789L, null);

        assertThrows(UserInputsException.class, () -> reviewService.create(invalidReview));
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    @DisplayName("Create Review With Too High Rating Throws UserInputsException")
    void create_ReviewWithTooHighRating_ThrowsUserInputsException() {
        Review invalidReview = new Review("1", testPost, testUser, 10, "Too high rating", 123456789L, null);

        assertThrows(UserInputsException.class, () -> reviewService.create(invalidReview));
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    @DisplayName("Get All Reviews Returns Reviews In Correct Order")
    void getAll_ReturnsReviewsInCorrectOrder() {
        when(reviewRepository.findAll()).thenReturn(testReviews);

        List<Review> result = reviewService.getAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(testReview.getId(), result.get(0).getId());
        assertEquals(testReview2.getId(), result.get(1).getId());
        verify(reviewRepository).findAll();
    }

    @Test
    @DisplayName("Get Average Rating For Post With Multiple Reviews Returns Correct Average")
    void getAverageRatingForPost_WithMultipleReviews_ReturnsCorrectAverage() {
        when(reviewRepository.calculateAverageRating(testPost.getId())).thenReturn(4.5);

        double result = reviewService.getAverageRatingForPost(testPost.getId());

        assertEquals(4.5, result, 0.01);
        verify(reviewRepository).calculateAverageRating(testPost.getId());
    }
}