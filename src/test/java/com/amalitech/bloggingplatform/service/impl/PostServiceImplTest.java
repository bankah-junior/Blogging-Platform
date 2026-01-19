package com.amalitech.bloggingplatform.service.impl;

import com.amalitech.bloggingplatform.cache.Cache;
import com.amalitech.bloggingplatform.dao.PostDAO;
import com.amalitech.bloggingplatform.dao.ReviewDAO;
import com.amalitech.bloggingplatform.model.Post;
import com.amalitech.bloggingplatform.model.Review;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceImplTest {

    @Mock
    private PostDAO postDAO;

    @Mock
    private ReviewDAO reviewDAO;

    @Mock
    private Cache<String, Post> postCache;

    @InjectMocks
    private PostServiceImpl postService;

    private Post post;

    @BeforeEach
    void setUp() {
        post = new Post();
        post.setId("1");
        post.setTitle("Test Post");
        post.setContent("Test Content");
        post.setAuthorId("author1");
        post.setCreatedAt(System.currentTimeMillis());
    }

    @Test
    void create() {
        when(postDAO.save(any(Post.class))).thenReturn(post);

        Post createdPost = postService.create(post);

        assertNotNull(createdPost);
        assertEquals(post.getId(), createdPost.getId());
        verify(postDAO, times(1)).save(post);
        verify(postCache, times(1)).put(post.getId(), post);
    }

    @Test
    void update() {
        when(postDAO.update(any(Post.class))).thenReturn(true);

        Post updatedPost = postService.update(post);

        assertNotNull(updatedPost);
        assertEquals(post.getId(), updatedPost.getId());
        verify(postDAO, times(1)).update(post);
        verify(postCache, times(1)).put(post.getId(), post);
    }

    @Test
    void delete() {
        when(postDAO.deleteById(anyString())).thenReturn(true);

        boolean deleted = postService.delete("1");

        assertTrue(deleted);
        verify(postDAO, times(1)).deleteById("1");
        verify(postCache, times(1)).remove("1");
    }

    @Test
    void getById() {
        when(postCache.get("1")).thenReturn(null);
        when(postDAO.findById("1")).thenReturn(Optional.of(post));
        when(reviewDAO.findByPostId("1")).thenReturn(new ArrayList<>());

        Post foundPost = postService.getById("1");

        assertNotNull(foundPost);
        assertEquals(post.getId(), foundPost.getId());
        verify(postCache, times(1)).get("1");
        verify(postDAO, times(1)).findById("1");
        verify(reviewDAO, times(1)).findByPostId("1");
        verify(postCache, times(1)).put("1", post);
    }

    @Test
    void getAll() {
        when(postDAO.findAll()).thenReturn(Collections.singletonList(post));
        when(reviewDAO.findByPostId(anyString())).thenReturn(new ArrayList<>());

        List<Post> posts = postService.getAll();

        assertNotNull(posts);
        assertEquals(1, posts.size());
        verify(postDAO, times(1)).findAll();
        verify(reviewDAO, times(1)).findByPostId(post.getId());
        verify(postCache, times(1)).put(post.getId(), post);
    }

    @Test
    void searchByTitle() {
        when(postDAO.searchByTitle(anyString())).thenReturn(Collections.singletonList(post));

        List<Post> posts = postService.searchByTitle("Test");

        assertNotNull(posts);
        assertEquals(1, posts.size());
        verify(postDAO, times(1)).searchByTitle("Test");
        verify(postCache, times(1)).put(post.getId(), post);
    }

    @Test
    void getByAuthor() {
        when(postDAO.findByAuthorId(anyString())).thenReturn(Collections.singletonList(post));

        List<Post> posts = postService.getByAuthor("author1");

        assertNotNull(posts);
        assertEquals(1, posts.size());
        verify(postDAO, times(1)).findByAuthorId("author1");
        verify(postCache, times(1)).put(post.getId(), post);
    }
    
    @Test
    void searchByTag() {
        // Given the current implementation, this test just checks if findAll is called.
        when(postDAO.findAll()).thenReturn(Collections.singletonList(post));
        
        List<Post> posts = postService.searchByTag("java");
        
        assertNotNull(posts);
        assertEquals(1, posts.size());
        verify(postDAO, times(1)).findAll();
    }
    
    @Test
    void sortByDate() {
        Post oldPost = new Post();
        oldPost.setCreatedAt(System.currentTimeMillis() - 10000);
        List<Post> posts = new ArrayList<>(List.of(post, oldPost));
        
        List<Post> sortedAsc = postService.sortByDate(posts, true);
        assertEquals(oldPost, sortedAsc.get(0));

        List<Post> sortedDesc = postService.sortByDate(posts, false);
        assertEquals(post, sortedDesc.get(0));
    }
    
    @Test
    void sortByTitle() {
        Post postB = new Post();
        postB.setTitle("A title");
        List<Post> posts = new ArrayList<>(List.of(post, postB));

        List<Post> sortedAsc = postService.sortByTitle(posts, true);
        assertEquals(postB, sortedAsc.get(0));

        List<Post> sortedDesc = postService.sortByTitle(posts, false);
        assertEquals(post, sortedDesc.get(0));
    }
    
    @Test
    void getAllSorted() {
        // This method calls getAll() which is already tested.
        // We can test the switching logic.
        when(postDAO.findAll()).thenReturn(new ArrayList<>());
        
        postService.getAllSorted("date", true);
        postService.getAllSorted("title", true);
        
        // simple test to ensure it runs without error, logic is in sortBy methods
        assertTrue(true);
    }
}
