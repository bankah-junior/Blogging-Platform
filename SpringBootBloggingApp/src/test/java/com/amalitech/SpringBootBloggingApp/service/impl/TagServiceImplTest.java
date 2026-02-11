package com.amalitech.SpringBootBloggingApp.service.impl;

import com.amalitech.SpringBootBloggingApp.cache.Cache;
import com.amalitech.SpringBootBloggingApp.model.entity.Tag;
import com.amalitech.SpringBootBloggingApp.model.entity.User;
import com.amalitech.SpringBootBloggingApp.repository.TagRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TagServiceImplTest {

    @Mock
    private TagRepository tagRepository;

    @Mock
    private Cache<String, User> userCache;

    @InjectMocks
    private TagServiceImpl tagService;

    private Tag testTag;
    private Tag testTag2;
    private List<Tag> testTags;

    @BeforeEach
    void setUp() {
        testTag = new Tag("697880f998594d7de95b36a4", "Java");
        testTag2 = new Tag("697880f998594d7de95b36a5", "Spring");
        testTags = List.of(testTag, testTag2);
    }

    @Test
    @DisplayName("Create Tag with valid name returns tag")
    void create_ValidTag_ReturnsTag() {
        when(tagRepository.save(any(Tag.class))).thenReturn(testTag);

        Tag result = tagService.create(testTag);

        assertNotNull(result);
        assertEquals(testTag.getId(), result.getId());
        assertEquals(testTag.getName(), result.getName());
        verify(tagRepository).save(testTag);
    }

    @Test
    @DisplayName("Create Tag with invalid name throws UserInputsException")
    void create_InvalidTagName_ThrowsUserInputsException() {
        Tag invalidTag = new Tag("asdf123qwerty1", "s");

        assertThrows(UserInputsException.class, () -> tagService.create(invalidTag));
        verify(tagRepository, never()).save(any(Tag.class));
    }

    @Test
    @DisplayName("Create Tag with null name throws UserInputsException")
    void create_NullTagName_ThrowsUserInputsException() {
        Tag invalidTag = new Tag("1", null);

        assertThrows(UserInputsException.class, () -> tagService.create(invalidTag));
        verify(tagRepository, never()).save(any(Tag.class));
    }

    @Test
    @DisplayName("Create Tag with invalid name format throws UserInputsException")
    void create_InvalidTagNameFormat_ThrowsUserInputsException() {
        Tag invalidTag = new Tag("asdf123qwerty4", "Invalid Tag Name With Spaces");

        assertThrows(UserInputsException.class, () -> tagService.create(invalidTag));
        verify(tagRepository, never()).save(any(Tag.class));
    }

    @Test
    @DisplayName("Get Tag by name with valid name returns tag")
    void getByName_ValidTagName_ReturnsTag() {
        when(tagRepository.findByName("java")).thenReturn(Optional.of(testTag));

        Tag result = tagService.getByName("java");

        assertNotNull(result);
        assertEquals(testTag.getId(), result.getId());
        assertEquals(testTag.getName(), result.getName());
        verify(tagRepository).findByName("java");
    }

    @Test
    @DisplayName("Get Tag by name with invalid name throws UserInputsException")
    void getByName_InvalidTagName_ThrowsUserInputsException() {
        assertThrows(UserInputsException.class, () -> tagService.getByName(""));
        verify(tagRepository, never()).findByName(anyString());
    }

    @Test
    @DisplayName("Get Tag by name with tag not found returns null")
    void getByName_TagNotFound_ReturnsNull() {
        when(tagRepository.findByName("nonexistent")).thenReturn(Optional.empty());

        Tag result = tagService.getByName("nonexistent");

        assertNull(result);
        verify(tagRepository).findByName("nonexistent");
    }

    @Test
    @DisplayName("Get all Tags returns list of tags")
    void getAll_ReturnsListOfTags() {
        when(tagRepository.findAll()).thenReturn(testTags);

        List<Tag> result = tagService.getAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Java", result.get(0).getName());
        assertEquals("Spring", result.get(1).getName());
        verify(tagRepository).findAll();
    }

    @Test
    @DisplayName("Get all Tags returns empty list when no tags exist")
    void getAll_EmptyList_ReturnsEmptyList() {
        when(tagRepository.findAll()).thenReturn(List.of());

        List<Tag> result = tagService.getAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(tagRepository).findAll();
    }

    @Test
    @DisplayName("Assign Tag to Post with valid ids calls repository assignTagToPost")
    void assignTagToPost_ValidIds_CallsRepositoryAssignTagToPost() {
        doNothing().when(tagRepository).assignTagToPost("6978db137edabfbdc62450b3", testTag.getId());

        tagService.assignTagToPost("6978db137edabfbdc62450b3", testTag.getId());

        verify(tagRepository).assignTagToPost("6978db137edabfbdc62450b3", testTag.getId());
    }

    @Test
    @DisplayName("Assign Tag to Post with invalid post id throws UserInputsException")
    void assignTagToPost_InvalidPostId_ThrowsUserInputsException() {
        assertThrows(UserInputsException.class, () -> tagService.assignTagToPost("invalid-id", "tag1"));
        verify(tagRepository, never()).assignTagToPost(anyString(), anyString());
    }

    @Test
    @DisplayName("Assign Tag to Post with invalid tag id throws UserInputsException")
    void assignTagToPost_InvalidTagId_ThrowsUserInputsException() {
        assertThrows(UserInputsException.class, () -> tagService.assignTagToPost("post1", "invalid-id"));
        verify(tagRepository, never()).assignTagToPost(anyString(), anyString());
    }

    @Test
    @DisplayName("Assign Tag to Post with both ids invalid throws UserInputsException")
    void assignTagToPost_BothIdsInvalid_ThrowsUserInputsException() {
        assertThrows(UserInputsException.class, () -> tagService.assignTagToPost("invalid-id", "invalid-id"));
        verify(tagRepository, never()).assignTagToPost(anyString(), anyString());
    }

    @Test
    @DisplayName("Get Tags by Post with valid post id returns list of tags")
    void getTagsByPost_ValidPostId_ReturnsPostTags() {
        List<Tag> expectedTags = List.of(testTag);
        when(tagRepository.findTagsByPostId("6978db137edabfbdc62450b3")).thenReturn(expectedTags);

        List<Tag> result = tagService.getTagsByPost("6978db137edabfbdc62450b3");
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Java", result.get(0).getName());
        verify(tagRepository).findTagsByPostId("6978db137edabfbdc62450b3");
    }

    @Test
    @DisplayName("Get Tags by Post with invalid post id throws UserInputsException")
    void getTagsByPost_InvalidPostId_ThrowsUserInputsException() {
        assertThrows(UserInputsException.class, () -> tagService.getTagsByPost("invalid-id"));
        verify(tagRepository, never()).findTagsByPostId(anyString());
    }

    @Test
    @DisplayName("Get Tags by Post with no tags assigned returns empty list")
    void getTagsByPost_NoTags_ReturnsEmptyList() {
        when(tagRepository.findTagsByPostId("6978db137edabfbdc62450b3")).thenReturn(List.of());

        List<Tag> result = tagService.getTagsByPost("6978db137edabfbdc62450b3");
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(tagRepository).findTagsByPostId("6978db137edabfbdc62450b3");
    }

    @Test
    @DisplayName("Unassign All Tags from Post with valid post id calls repository unassignAllTagsFromPost")
    void unassignAllTagsFromPost_ValidPostId_CallsRepositoryUnassignAllTagsFromPost() {
        doNothing().when(tagRepository).unassignAllTagsFromPost("6978db137edabfbdc62450b3");

        tagService.unassignAllTagsFromPost("6978db137edabfbdc62450b3");

        verify(tagRepository).unassignAllTagsFromPost("6978db137edabfbdc62450b3");
    }

    @Test
    @DisplayName("Unassign All Tags from Post with invalid post id throws UserInputsException")
    void unassignAllTagsFromPost_InvalidPostId_ThrowsUserInputsException() {
        assertThrows(UserInputsException.class, () -> tagService.unassignAllTagsFromPost("invalid-id"));
        verify(tagRepository, never()).unassignAllTagsFromPost(anyString());
    }

    @Test
    @DisplayName("Create Tag with valid name calls repository save")
    void create_TagWithValidName_CallsRepositorySave() {
        Tag validTag = new Tag("asdf123qwertyb", "python");
        when(tagRepository.save(validTag)).thenReturn(validTag);

        Tag result = tagService.create(validTag);

        assertNotNull(result);
        verify(tagRepository).save(validTag);
    }

    @Test
    @DisplayName("Get Tag by Name with valid name calls repository findByName")
    void getByName_TagWithValidName_CallsRepositoryFindByName() {
        when(tagRepository.findByName("Spring")).thenReturn(Optional.of(testTag2));

        Tag result = tagService.getByName("Spring");

        assertNotNull(result);
        assertEquals("Spring", result.getName());
        verify(tagRepository).findByName("Spring");
    }

    @Test
    @DisplayName("Assign Tag to Post with valid tag id and post id calls repository assignTagToPost")
    void assignTagToPost_TagWithValidIds_CallsRepositoryAssignTagToPost() {
        doNothing().when(tagRepository).assignTagToPost("6978db137edabfbdc62450b3", testTag2.getId());

        tagService.assignTagToPost("6978db137edabfbdc62450b3", testTag2.getId());

        verify(tagRepository).assignTagToPost("6978db137edabfbdc62450b3", testTag2.getId());
    }

    @Test
    @DisplayName("Get Tags by Post with valid post id returns list of tags")
    void getTagsByPost_TagWithValidPostId_CallsRepositoryFindTagsByPostId() {
        when(tagRepository.findTagsByPostId("6978db137edabfbdc62450b3")).thenReturn(List.of(testTag, testTag2));
        List<Tag> result = tagService.getTagsByPost("6978db137edabfbdc62450b3");

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(tagRepository).findTagsByPostId("6978db137edabfbdc62450b3");
    }

    @Test
    @DisplayName("Unassign All Tags from Post with valid post id calls repository unassignAllTagsFromPost")
    void unassignAllTagsFromPost_TagWithValidPostId_CallsRepositoryUnassignAllTagsFromPost() {
        doNothing().when(tagRepository).unassignAllTagsFromPost("6978db137edabfbdc62450b3");

        tagService.unassignAllTagsFromPost("6978db137edabfbdc62450b3");

        verify(tagRepository).unassignAllTagsFromPost("6978db137edabfbdc62450b3");
    }

    @Test
    @DisplayName("Create Tag with special characters in name throws UserInputsException")
    void create_TagWithSpecialCharactersInName_ThrowsUserInputsException() {
        Tag invalidTag = new Tag("asdf123qwertyb", "java@springijustdon'tgetthistagname");

        assertThrows(UserInputsException.class, () -> tagService.create(invalidTag));
        verify(tagRepository, never()).save(any(Tag.class));
    }

    @Test
    @DisplayName("Get Tag by Name with special characters in name throws UserInputsException")
    void getByName_TagWithSpecialCharactersInName_ThrowsUserInputsException() {
        assertThrows(UserInputsException.class, () -> tagService.getByName("java@spring"));
        verify(tagRepository, never()).findByName(anyString());
    }

    @Test
    @DisplayName("Get All Tags returns tags in correct order")
    void getAll_ReturnsTagsInCorrectOrder() {
        when(tagRepository.findAll()).thenReturn(testTags);

        List<Tag> result = tagService.getAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Java", result.get(0).getName());
        assertEquals("Spring", result.get(1).getName());
        verify(tagRepository).findAll();
    }
}