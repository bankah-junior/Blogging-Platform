package com.amalitech.bloggingplatform.controller;

import com.amalitech.bloggingplatform.model.User;
import com.amalitech.bloggingplatform.service.impl.UserServiceImpl;
import com.amalitech.bloggingplatform.utils.exceptions.UserInputsException;

import java.util.List;

import static com.amalitech.bloggingplatform.utils.ValidationUtils.*;

public class UserController {
    private final UserServiceImpl userServiceImpl;

    public UserController() {
        this.userServiceImpl = new UserServiceImpl();
    }

    /**
     * Login a user.
     *
     * @param email    The user's email.
     * @param password The user's password.
     * @return The logged-in user model or null if credentials are invalid.
     */
    public User login(String email, String password) {
        if (email == null || password == null) {
            throw new UserInputsException("Email and password cannot be null.");
        }
        if (!isEmailValid(email)) {
            throw new UserInputsException("Invalid email format.");
        }
        User user = userServiceImpl.login(email, password);
        if (user == null) {
            throw new UserInputsException("Invalid email or password.");
        }
        System.out.println("Login successful: " + user.toString());
        return user;
    }

     /**
     * Register a new user.
     *
     * @param user The user model to register.
     * @return The registered user model.
     */
    public User register(User user) {
        if (user.getEmail() == null || user.getPasswordHash() == null || user.getUsername() == null) {
            throw new UserInputsException("Email, password hash, and username cannot be null.");
        }
        if (!isEmailValid(user.getEmail())) {
            throw new UserInputsException("Invalid email format.");
        }
        if (!isPasswordValid(user.getPasswordHash())) {
            throw new UserInputsException("Password must be at least 8 characters long, " +
                    "contain at least one uppercase and lowercase letter, " +
                    "one digit, and one special character");
        }
        if (!isUsernameValid(user.getUsername())) {
            throw new UserInputsException("Username must be 3-60 characters long and can contain letters, numbers, dots, underscores, and hyphens.");
        }
        User newUser = userServiceImpl.create(user);
        if (newUser == null) {
            System.out.println("User already exists with this email.");
            return null;
        }
        System.out.println(newUser.toString());
        return newUser;
    }

    /**
     * Update an existing user.
     *
     * @param user The user model with updated information.
     * @return The updated user model.
     */
    public User update(User user) {
        if (user.getId() == null) {
            throw new UserInputsException("User ID cannot be null.");
        }
        if (!isValidObjectId(user.getId())) {
            throw new UserInputsException("Invalid user ID format.");
        }
        User updatedUser = userServiceImpl.getById(user.getId());
        if (updatedUser == null) {
            throw new UserInputsException("User not found with ID: " + user.getId());
        }
        System.out.println("Updating user: " + user.toString());
        return userServiceImpl.update(user);
    }

    /**
     * Delete a user by their ID.
     *
     * @param userId The ID of the user to delete.
     * @return True if the user was deleted, false otherwise.
     */
    public boolean delete(String userId) {
        if (!isValidObjectId(userId)) {
            throw new UserInputsException("Invalid user ID format.");
        }
        System.out.println("Deleting user with ID: " + userId);
        return userServiceImpl.delete(userId);
    }

    /**
     * Retrieve a user by their ID.
     * @param userId The ID of the user to retrieve.
     * @return The user model or null if the ID is invalid.
     */
    public User getById(String userId) {
        if (!isValidObjectId(userId)) {
            throw new UserInputsException("Invalid user ID format.");
        }
        User user = userServiceImpl.getById(userId);
        if (user == null) {
            throw new UserInputsException("User not found with ID: " + userId);
        }
        System.out.println(user.toString());
        return user;
    }

    /**
     * Retrieve a user by their email.
     * @param email The email of the user to retrieve.
     * @return The user model or null if the email is invalid.
     */
    public User getByEmail(String email) {
        if (!isEmailValid(email)) {
            throw new UserInputsException("Invalid email format.");
        }
        User user = userServiceImpl.getByEmail(email);
        if (user == null) {
            System.out.println("No user found with email: " + email);
        }
        System.out.println(user.toString());
        return user;
    }

     /**
     * Retrieve all users.
     * @return A list of all user models.
     */
    public List<User> getAll() {
        return userServiceImpl.getAll();
    }
}
