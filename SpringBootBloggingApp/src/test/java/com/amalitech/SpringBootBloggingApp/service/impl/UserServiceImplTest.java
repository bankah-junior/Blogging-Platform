package com.amalitech.SpringBootBloggingApp.service.impl;

import com.amalitech.SpringBootBloggingApp.cache.Cache;
import com.amalitech.SpringBootBloggingApp.model.dto.request.LoginRequest;
import com.amalitech.SpringBootBloggingApp.model.dto.request.RegisterRequest;
import com.amalitech.SpringBootBloggingApp.model.dto.request.UpdateUserDetailRequest;
import com.amalitech.SpringBootBloggingApp.model.dto.request.UpdateUserRequest;
import com.amalitech.SpringBootBloggingApp.model.dto.response.UserResponse;
import com.amalitech.SpringBootBloggingApp.model.entity.User;
import com.amalitech.SpringBootBloggingApp.repository.impl.UserRepositoryImpl;
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
class UserServiceImplTest {

    @Mock
    private UserRepositoryImpl userRepository;

    @Mock
    private Cache<String, User> userCache;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private RegisterRequest registerRequest;
    private UpdateUserRequest updateUserRequest;
    private UpdateUserDetailRequest updateUserDetailRequest;

    @BeforeEach
    void setUp() {
        testUser = new User("69787e41808ddd7b66d3a6f6", "testuser", "test@example.com", "Hashed@1", System.currentTimeMillis(), null);
        registerRequest = new RegisterRequest("test@example.com", "testuser", "Passwo@1");
        updateUserRequest = new UpdateUserRequest("69787e41808ddd7b66d3a6f6", "update@mail.com", "updateduser", "NewPas@1");
        updateUserDetailRequest = new UpdateUserDetailRequest("69787e41808ddd7b66d3a6f6", "updated@example.com", "updateduser");
    }

    @Test
    @DisplayName("Create user with valid input returns user response")
    void create_ValidUser_ReturnsUserResponse() {
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserResponse result = userService.create(registerRequest);

        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getUsername(), result.getUsername());
        assertEquals(testUser.getEmail(), result.getEmail());
        assertNotNull(result.getToken());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Create user with invalid username throws user inputs exception")
    void create_InvalidUsername_ThrowsUserInputsException() {
        RegisterRequest invalidRequest = new RegisterRequest("testuser", "", "Passwo@1");

        assertThrows(UserInputsException.class, () -> userService.create(invalidRequest));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Create user with invalid email throws user inputs exception")
    void create_InvalidEmail_ThrowsUserInputsException() {
        RegisterRequest invalidRequest = new RegisterRequest("", "invalid-username", "Passwo@1");

        assertThrows(UserInputsException.class, () -> userService.create(invalidRequest));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Create user with invalid password throws user inputs exception")
    void create_InvalidPassword_ThrowsUserInputsException() {
        RegisterRequest invalidRequest = new RegisterRequest("test@example.com", "testuser", "123");

        assertThrows(UserInputsException.class, () -> userService.create(invalidRequest));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Login with non-existent email throws user inputs exception")
    void login_EmailNotFound_ThrowsUserInputsException() {
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        LoginRequest invalidLoginRequest = new LoginRequest("nonexistent@example.com", "password123");

        assertThrows(UserInputsException.class, () -> userService.login(invalidLoginRequest));
        verify(userRepository).findByEmail("nonexistent@example.com");
    }

    @Test
    @DisplayName("Update user with invalid username throws user inputs exception")
    void update_InvalidUsername_ThrowsUserInputsException() {
        UpdateUserRequest invalidRequest = new UpdateUserRequest(testUser.getId(), "updated@example.com", "", "newpassword123");

        assertThrows(UserInputsException.class, () -> userService.update(invalidRequest));
        verify(userRepository, never()).update(any(User.class));
    }

    @Test
    @DisplayName("Delete user with valid input returns true")
    void delete_ValidUser_ReturnsTrue() {
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(userRepository.deleteById(testUser.getId())).thenReturn(true);

        boolean result = userService.delete(testUser.getId());

        assertTrue(result);
        verify(userRepository).findById(testUser.getId());
        verify(userRepository).deleteById(testUser.getId());
        verify(userCache).remove(testUser.getId());
    }

    @Test
    @DisplayName("Delete user with non-existent id throws user inputs exception")
    void delete_UserNotFound_ThrowsUserInputsException() {
        when(userRepository.findById("nonexistent")).thenReturn(Optional.empty());

        assertThrows(UserInputsException.class, () -> userService.delete("nonexistent"));
        verify(userRepository).findById("nonexistent");
        verify(userRepository, never()).deleteById(anyString());
    }

    @Test
    @DisplayName("Get user by id with valid id returns user response")
    void getById_ValidId_ReturnsUserResponse() {
        when(userCache.get(testUser.getId())).thenReturn(null);
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));

        UserResponse result = userService.getById(testUser.getId());

        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getUsername(), result.getUsername());
        assertEquals(testUser.getEmail(), result.getEmail());
        assertNotNull(result.getToken());
        verify(userCache).get(testUser.getId());
        verify(userRepository).findById(testUser.getId());
        verify(userCache).put(testUser.getId(), testUser);
    }

    @Test
    @DisplayName("Get user by id with non-existent id throws user inputs exception")
    void getById_UserNotFound_ThrowsUserInputsException() {
        when(userCache.get("nonexistent")).thenReturn(null);
        when(userRepository.findById("nonexistent")).thenReturn(Optional.empty());

        assertThrows(UserInputsException.class, () -> userService.getById("nonexistent"));
        verify(userCache).get("nonexistent");
        verify(userRepository).findById("nonexistent");
    }

    @Test
    @DisplayName("Get user by id with user in cache returns user response")
    void getById_UserInCache_ReturnsUserResponse() {
        when(userCache.get(testUser.getId())).thenReturn(testUser);

        UserResponse result = userService.getById(testUser.getId());

        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        verify(userCache).get(testUser.getId());
        verify(userRepository, never()).findById(anyString());
    }

    @Test
    @DisplayName("Get user by email with valid email returns user response")
    void getByEmail_ValidEmail_ReturnsUserResponse() {
        when(userCache.get("test@example.com")).thenReturn(null);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        UserResponse result = userService.getByEmail("test@example.com");

        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
        verify(userCache).get("test@example.com");
        verify(userRepository).findByEmail("test@example.com");
        verify(userCache).put("test@example.com", testUser);
    }

    @Test
    @DisplayName("Get all users returns list of user responses")
    void getAll_ReturnsListOfUserResponses() {
        List<User> users = List.of(testUser, new User("qwertyuiop148", "user2", "user2@example.com", "Passwo@2", System.currentTimeMillis(), null));
        when(userRepository.findAll()).thenReturn(users);

        List<UserResponse> result = userService.getAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("testuser", result.get(0).getUsername());
        assertEquals("user2", result.get(1).getUsername());
        verify(userRepository).findAll();
    }

    @Test
    @DisplayName("Update user details with valid input returns true")
    void updateUserDetails_ValidUser_ReturnsTrue() {
        when(userRepository.updateUserDetails(eq(testUser.getId()), any(User.class))).thenReturn(true);

        boolean result = userService.updateUserDetails(testUser.getId(), updateUserDetailRequest);

        assertTrue(result);
        verify(userRepository).updateUserDetails(eq(testUser.getId()), any(User.class));
    }

    @Test
    @DisplayName("Update user details with invalid user id throws user inputs exception")
    void updateUserDetails_InvalidUserId_ThrowsUserInputsException() {
        assertThrows(UserInputsException.class, () -> userService.updateUserDetails("invalid-id", updateUserDetailRequest));
        verify(userRepository, never()).updateUserDetails(anyString(), any(User.class));
    }

    @Test
    @DisplayName("Change password with invalid user id throws user inputs exception")
    void changePassword_InvalidUserId_ThrowsUserInputsException() {
        assertThrows(UserInputsException.class, () -> userService.changePassword("invalid-id", "oldpassword", "newpassword"));
        verify(userRepository, never()).findById(anyString());
    }

    @Test
    @DisplayName("Get user by username with valid username returns user response")
    void getByUsername_ValidUsername_ReturnsUserResponse() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        UserResponse result = userService.getByUsername("testuser");

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userRepository).findByUsername("testuser");
    }

    @Test
    @DisplayName("Get user by username with invalid username throws user inputs exception")
    void getByUsername_InvalidUsername_ThrowsUserInputsException() {
        assertThrows(UserInputsException.class, () -> userService.getByUsername(""));
        verify(userRepository, never()).findByUsername(anyString());
    }

    @Test
    @DisplayName("Get user by username with user not found throws user inputs exception")
    void getByUsername_UserNotFound_ThrowsUserInputsException() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        assertThrows(UserInputsException.class, () -> userService.getByUsername("nonexistent"));
        verify(userRepository).findByUsername("nonexistent");
    }
}