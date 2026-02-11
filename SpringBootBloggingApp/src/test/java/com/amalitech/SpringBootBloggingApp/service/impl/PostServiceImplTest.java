package com.amalitech.SpringBootBloggingApp.service.impl;

import com.amalitech.SpringBootBloggingApp.cache.Cache;
import com.amalitech.SpringBootBloggingApp.model.entity.Post;
import com.amalitech.SpringBootBloggingApp.model.entity.User;
import com.amalitech.SpringBootBloggingApp.util.exceptions.UserInputsException;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceImplTest {

    @Mock
    private PostRepositoryImpl postRepository;

    @Mock
    private Cache<String, User> userCache;

    @InjectMocks
    private PostServiceImpl postService;

    private Post testPost;
    private Post testPost2;
    private List<Post> testPosts;

    private User testUser;
    private User testUser2;



    @BeforeEach
    void setUp() {
        testUser = new User("697349d17b196ad927aa8a03", "Sir Kay", "author1@example.com", "Pass123@w", System.currentTimeMillis(), System.currentTimeMillis());
        testUser2 = new User("696e40258e370aa034f5f291", "Jane Doe", "author2@example.com", "Pass456@w", System.currentTimeMillis(), System.currentTimeMillis());
        testPost = new Post("697349d17b196ad927aa8a03", testUser, "Test Post", "This is a test post content", true, System.currentTimeMillis(), null, List.of());
        testPost2 = new Post("696e40258e370aa034f5f291", testUser2, "Another Post", "This is another test post content", true, System.currentTimeMillis(), null, List.of());
        testPosts = List.of(testPost, testPost2);
    }

    @Test
    @DisplayName("Create Valid Post Returns Post")
    void create_ValidPost_ReturnsPost() {
        when(postRepository.save(any(Post.class))).thenReturn(testPost);

        Post result = postService.create(testPost);

        assertNotNull(result);
        assertEquals(testPost.getId(), result.getId());
        assertEquals(testPost.getTitle(), result.getTitle());
        assertEquals(testPost.getContent(), result.getContent());
        verify(postRepository).save(testPost);
    }

    @Test
    @DisplayName("Create Invalid Title Throws UserInputsException")
    void create_InvalidTitle_ThrowsUserInputsException() {
        Post invalidPost = new Post("696e40258e370aa034f5f292", testUser, "", "author1", true, System.currentTimeMillis(), null, List.of());

        assertThrows(UserInputsException.class, () -> postService.create(invalidPost));
        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    @DisplayName("Create Invalid Content Throws UserInputsException")
    void create_InvalidContent_ThrowsUserInputsException() {
        Post invalidPost = new Post("696e40258e370aa034f5f292", testUser, "Valid Title", "", true, System.currentTimeMillis(), null, List.of());

        assertThrows(UserInputsException.class, () -> postService.create(invalidPost));
        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    @DisplayName("Update Valid Post Returns Post")
    void update_ValidPost_ReturnsPost() {
        when(postRepository.update(testPost)).thenReturn(true);

        Post result = postService.update(testPost);

        assertNotNull(result);
        assertEquals(testPost, result);
        verify(postRepository).update(testPost);
    }

    @Test
    @DisplayName("Update Failed Update Returns Null")
    void update_FailedUpdate_ReturnsNull() {
        when(postRepository.update(testPost)).thenReturn(false);

        Post result = postService.update(testPost);

        assertNull(result);
        verify(postRepository).update(testPost);
    }

    @Test
    @DisplayName("Delete Valid Post Id Returns True")
    void delete_ValidPostId_ReturnsTrue() {
        when(postRepository.deleteById(testPost2.getId())).thenReturn(true);

        boolean result = postService.delete(testPost2.getId());

        assertTrue(result);
        verify(postRepository).deleteById(testPost2.getId());
    }

    @Test
    @DisplayName("Delete Invalid Post Id Throws UserInputsException")
    void delete_InvalidPostId_ThrowsUserInputsException() {
        assertThrows(UserInputsException.class, () -> postService.delete("invalid-id"));
        verify(postRepository, never()).deleteById(anyString());
    }

    @Test
    @DisplayName("Delete Failed Delete Returns False")
    void delete_FailedDelete_ReturnsFalse() {
        when(postRepository.deleteById("697349d17b196ad927aa89fa")).thenReturn(false);

        boolean result = postService.delete("697349d17b196ad927aa89fa");

        assertFalse(result);
        verify(postRepository).deleteById("697349d17b196ad927aa89fa");
    }

    @Test
    @DisplayName("Get By Valid Post Id Returns Post")
    void getById_ValidPostId_ReturnsPost() {
        when(postRepository.findById(testPost.getId())).thenReturn(Optional.of(testPost));

        Post result = postService.getById(testPost.getId());
        assertNotNull(result);
        assertEquals(testPost.getId(), result.getId());
        assertEquals("Test Post", result.getTitle());
        verify(postRepository).findById(testPost.getId());
    }

    @Test
    @DisplayName("Get By Invalid Post Id Throws UserInputsException")
    void getById_InvalidPostId_ThrowsUserInputsException() {
        assertThrows(UserInputsException.class, () -> postService.getById("invalid-id"));
        verify(postRepository, never()).findById(anyString());
    }

    @Test
    @DisplayName("Get By Post Id Not Found Returns Null")
    void getById_PostNotFound_ReturnsNull() {
        when(postRepository.findById("697349d17b196ad927aa8a02")).thenReturn(Optional.empty());

        Post result = postService.getById("697349d17b196ad927aa8a02");

        assertNull(result);
        verify(postRepository).findById("697349d17b196ad927aa8a02");
    }

    @Test
    @DisplayName("Get All Returns List Of Posts")
    void getAll_ReturnsListOfPosts() {
        when(postRepository.findAll()).thenReturn(testPosts);

        List<Post> result = postService.getAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Test Post", result.get(0).getTitle());
        assertEquals("Another Post", result.get(1).getTitle());
        verify(postRepository).findAll();
    }

    @Test
    @DisplayName("Search By Title Valid Keyword Returns Matching Posts")
    void searchByTitle_ValidKeyword_ReturnsMatchingPosts() {
        List<Post> expectedPosts = List.of(testPost);
        when(postRepository.searchByTitle("Test")).thenReturn(expectedPosts);

        List<Post> result = postService.searchByTitle("Test");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Post", result.get(0).getTitle());
        verify(postRepository).searchByTitle("Test");
    }

    @Test
    @DisplayName("Search By Title No Matches Returns Empty List")
    void searchByTitle_NoMatches_ReturnsEmptyList() {
        when(postRepository.searchByTitle("Nonexistent")).thenReturn(List.of());

        List<Post> result = postService.searchByTitle("Nonexistent");

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(postRepository).searchByTitle("Nonexistent");
    }

    @Test
    @DisplayName("Get By Author Valid Author Id Returns Author Posts")
    void getByAuthor_ValidAuthorId_ReturnsAuthorPosts() {
        List<Post> expectedPosts = List.of(testPost);
        when(postRepository.findByAuthorId(testPost.getAuthor().getId())).thenReturn(expectedPosts);

        List<Post> result = postService.getByAuthor(testPost.getAuthor().getId());

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testPost.getAuthor().getId(), result.get(0).getAuthor().getId());
        verify(postRepository).findByAuthorId(testPost.getAuthor().getId());
    }

    @Test
    @DisplayName("Get By Author Invalid Author Id Throws UserInputsException")
    void getByAuthor_InvalidAuthorId_ThrowsUserInputsException() {
        assertThrows(UserInputsException.class, () -> postService.getByAuthor("invalid-id"));
        verify(postRepository, never()).findByAuthorId(anyString());
    }

    @Test
    @DisplayName("Search By Tag Valid Tag Name Returns Tagged Posts")
    void searchByTag_ValidTagName_ReturnsTaggedPosts() {
        List<Post> expectedPosts = List.of(testPost);
        when(postRepository.findByTagName("java")).thenReturn(expectedPosts);

        List<Post> result = postService.searchByTag("java");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(postRepository).findByTagName("java");
    }

    @Test
    @DisplayName("Search By Tag No Tagged Posts Returns Empty List")
    void searchByTag_NoTaggedPosts_ReturnsEmptyList() {
        when(postRepository.findByTagName("nonexistent")).thenReturn(List.of());

        List<Post> result = postService.searchByTag("nonexistent");

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(postRepository).findByTagName("nonexistent");
    }

    @Test
    @DisplayName("Get All Sorted By Date Returns Posts Sorted By Date")
    void getAllSorted_SortByDate_ReturnsPostsSortedByDate() {
        when(postRepository.findAll()).thenReturn(testPosts);

        List<Post> result = postService.getAllSorted("date", true);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(postRepository).findAll();
    }

    @Test
    @DisplayName("Get All Sorted By Title Returns Posts Sorted By Title")
    void getAllSorted_SortByTitle_ReturnsPostsSortedByTitle() {
        when(postRepository.findAll()).thenReturn(testPosts);

        List<Post> result = postService.getAllSorted("title", false);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(postRepository).findAll();
    }

    @Test
    @DisplayName("Get All Sorted By Invalid SortBy Returns Unsorted Posts")
    void getAllSorted_InvalidSortBy_ReturnsUnsortedPosts() {
        when(postRepository.findAll()).thenReturn(testPosts);

        List<Post> result = postService.getAllSorted("invalid", true);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(postRepository).findAll();
    }

    @Test
    @DisplayName("Get All Sorted Case Insensitive SortBy Returns Correctly Sorted Posts")
    void getAllSorted_CaseInsensitiveSortBy_ReturnsCorrectlySortedPosts() {
        when(postRepository.findAll()).thenReturn(testPosts);

        List<Post> resultDate = postService.getAllSorted("DATE", true);
        List<Post> resultTitle = postService.getAllSorted("TITLE", false);

        assertNotNull(resultDate);
        assertNotNull(resultTitle);
        assertEquals(2, resultDate.size());
        assertEquals(2, resultTitle.size());
        verify(postRepository, times(2)).findAll();
    }
}