package com.amalitech.bloggingplatform.service.impl;

import com.amalitech.bloggingplatform.dao.CommentDAO;
import com.amalitech.bloggingplatform.model.Comment;
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
class CommentServiceImplTest {

    @Mock
    private CommentDAO commentDAO;

    @InjectMocks
    private CommentServiceImpl commentService;

    private Comment comment;

    @BeforeEach
    void setUp() {
        comment = new Comment();
        comment.setId("1");
        comment.setPostId("postId");
        comment.setUserId("userId");
        comment.setContent("Test comment");
    }

    @Test
    void create() {
        when(commentDAO.save(any(Comment.class))).thenReturn(comment);

        Comment createdComment = commentService.create(comment);

        assertNotNull(createdComment);
        assertEquals(comment.getId(), createdComment.getId());
        verify(commentDAO, times(1)).save(comment);
    }

    @Test
    void delete() {
        when(commentDAO.deleteById(anyString())).thenReturn(true);

        boolean deleted = commentService.delete("1");

        assertTrue(deleted);
        verify(commentDAO, times(1)).deleteById("1");
    }

    @Test
    void getByPost() {
        when(commentDAO.findByPostId(anyString())).thenReturn(Collections.singletonList(comment));

        List<Comment> comments = commentService.getByPost("postId");

        assertNotNull(comments);
        assertEquals(1, comments.size());
        verify(commentDAO, times(1)).findByPostId("postId");
    }

    @Test
    void getByUser() {
        // As per implementation, this currently returns an empty list.
        List<Comment> comments = commentService.getByUser("userId");

        assertNotNull(comments);
        assertTrue(comments.isEmpty());
    }
}
