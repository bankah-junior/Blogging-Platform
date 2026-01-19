package com.amalitech.bloggingplatform.service.impl;

import com.amalitech.bloggingplatform.dao.TagDAO;
import com.amalitech.bloggingplatform.model.Tag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TagServiceImplTest {

    @Mock
    private TagDAO tagDAO;

    @InjectMocks
    private TagServiceImpl tagService;

    private Tag tag;

    @BeforeEach
    void setUp() {
        tag = new Tag();
        tag.setId("1");
        tag.setName("java");
    }

    @Test
    void create() {
        when(tagDAO.save(any(Tag.class))).thenReturn(tag);

        Tag createdTag = tagService.create(tag);

        assertNotNull(createdTag);
        assertEquals(tag.getId(), createdTag.getId());
        verify(tagDAO, times(1)).save(tag);
    }

    @Test
    void getByName() {
        when(tagDAO.findByName(anyString())).thenReturn(Optional.of(tag));

        Tag foundTag = tagService.getByName("java");

        assertNotNull(foundTag);
        assertEquals(tag.getName(), foundTag.getName());
        verify(tagDAO, times(1)).findByName("java");
    }

    @Test
    void getAll() {
        when(tagDAO.findAll()).thenReturn(Collections.singletonList(tag));

        List<Tag> tags = tagService.getAll();

        assertNotNull(tags);
        assertEquals(1, tags.size());
        verify(tagDAO, times(1)).findAll();
    }

    @Test
    void assignTagToPost() {
        doNothing().when(tagDAO).assignTagToPost(anyString(), anyString());

        tagService.assignTagToPost("postId", "tagId");

        verify(tagDAO, times(1)).assignTagToPost("postId", "tagId");
    }

    @Test
    void getTagsByPost() {
        when(tagDAO.findTagsByPostId(anyString())).thenReturn(Collections.singletonList(tag));

        List<Tag> tags = tagService.getTagsByPost("postId");

        assertNotNull(tags);
        assertEquals(1, tags.size());
        verify(tagDAO, times(1)).findTagsByPostId("postId");
    }

    @Test
    void unassignAllTagsFromPost() {
        doNothing().when(tagDAO).unassignAllTagsFromPost(anyString());

        tagService.unassignAllTagsFromPost("postId");

        verify(tagDAO, times(1)).unassignAllTagsFromPost("postId");
    }
}
