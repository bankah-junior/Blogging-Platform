package com.amalitech.bloggingplatform.service.impl;

import com.amalitech.bloggingplatform.cache.Cache;
import com.amalitech.bloggingplatform.dao.UserDAO;
import com.amalitech.bloggingplatform.model.User;
import com.amalitech.bloggingplatform.utils.PasswordUtils;
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
class UserServiceImplTest {

    @Mock
    private UserDAO userDAO;

    @Mock
    private Cache<String, User> userCache;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId("1");
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPasswordHash(PasswordUtils.hashPassword("password"));
    }

    @Test
    void create() {
        when(userDAO.save(any(User.class))).thenReturn(user);

        User createdUser = userService.create(user);

        assertNotNull(createdUser);
        assertEquals(user.getId(), createdUser.getId());
        verify(userDAO, times(1)).save(user);
        verify(userCache, times(1)).put(user.getId(), user);
    }

    @Test
    void login() {
        when(userDAO.login(anyString(), anyString())).thenReturn(Optional.of(user));

        User loggedInUser = userService.login("test@example.com", "password");

        assertNotNull(loggedInUser);
        assertEquals(user.getEmail(), loggedInUser.getEmail());
        verify(userDAO, times(1)).login("test@example.com", "password");
    }

    @Test
    void update() {
        when(userDAO.update(any(User.class))).thenReturn(true);

        User updatedUser = userService.update(user);

        assertNotNull(updatedUser);
        assertEquals(user.getId(), updatedUser.getId());
        verify(userDAO, times(1)).update(user);
        verify(userCache, times(1)).put(user.getId(), user);
    }
    
    @Test
    void delete() {
        when(userDAO.deleteById(anyString())).thenReturn(true);

        boolean deleted = userService.delete("1");

        assertTrue(deleted);
        verify(userDAO, times(1)).deleteById("1");
        verify(userCache, times(1)).remove("1");
    }

    @Test
    void getById() {
        when(userDAO.findById(anyString())).thenReturn(Optional.of(user));

        User foundUser = userService.getById("1");

        assertNotNull(foundUser);
        assertEquals(user.getId(), foundUser.getId());
        verify(userDAO, times(1)).findById("1");
    }

    @Test
    void getByEmail() {
        when(userDAO.findByEmail(anyString())).thenReturn(Optional.of(user));

        User foundUser = userService.getByEmail("test@example.com");

        assertNotNull(foundUser);
        assertEquals(user.getEmail(), foundUser.getEmail());
        verify(userDAO, times(1)).findByEmail("test@example.com");
    }

    @Test
    void getAll() {
        when(userDAO.findAll()).thenReturn(Collections.singletonList(user));

        List<User> users = userService.getAll();

        assertNotNull(users);
        assertEquals(1, users.size());
        verify(userDAO, times(1)).findAll();
    }

    @Test
    void updateUserDetails() {
        when(userDAO.updateUserDetails(any(User.class))).thenReturn(true);

        boolean updated = userService.updateUserDetails(user);

        assertTrue(updated);
        verify(userDAO, times(1)).updateUserDetails(user);
        verify(userCache, times(1)).put(user.getId(), user);
    }

    @Test
    void changePassword() {
        when(userDAO.findById(anyString())).thenReturn(Optional.of(user));
        when(userDAO.updatePassword(anyString(), anyString())).thenReturn(true);

        boolean changed = userService.changePassword("1", "password", "newpassword");

        assertTrue(changed);
        verify(userDAO, times(1)).findById("1");
        verify(userDAO, times(1)).updatePassword(eq("1"), anyString());
        verify(userCache, times(1)).remove("1");
    }

    @Test
    void getByUsername() {
        when(userDAO.findByUsername(anyString())).thenReturn(Optional.of(user));

        User foundUser = userService.getByUsername("testuser");

        assertNotNull(foundUser);
        assertEquals(user.getUsername(), foundUser.getUsername());
        verify(userDAO, times(1)).findByUsername("testuser");
    }
}
