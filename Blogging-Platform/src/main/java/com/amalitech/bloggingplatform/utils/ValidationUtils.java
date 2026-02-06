package com.amalitech.bloggingplatform.utils;

import com.amalitech.bloggingplatform.utils.exceptions.UserInputsException;
import org.bson.types.ObjectId;

import java.util.regex.Pattern;

public final class ValidationUtils {

    private ValidationUtils() {
        // Utility class – prevent instantiation
    }

    /* =========================
       REGEX PATTERNS (Compiled for performance)
       ========================= */

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");

    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$");

    private static final Pattern USERNAME_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9._-]{3,60}$");

    private static final Pattern TAG_NAME_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_-]{2,30}$");


    /* =========================
       USER VALIDATION
       ========================= */

    /**
     * Validates email format.
     */
    public static boolean isEmailValid(String email) {
        return isNotBlank(email) && EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Validates password strength:
     * - Min 8 characters
     * - At least one uppercase letter, one lowercase letter, one digit, and one special character.
     */
    public static boolean isPasswordValid(String password) {
        return isNotBlank(password) && PASSWORD_PATTERN.matcher(password).matches();
    }

    /**
     * Validates username.
     * Must be 3-60 characters and can contain letters, numbers, dots, underscores, or hyphens.
     */
    public static boolean isUsernameValid(String username) {
        return isNotBlank(username) && USERNAME_PATTERN.matcher(username).matches();
    }

    /* =========================
       ID VALIDATION
       ========================= */

    /**
     * Validates MongoDB ObjectId string.
     */
    public static boolean isValidObjectId(String id) {
        return isNotBlank(id) && ObjectId.isValid(id);
    }

    /* =========================
       CONTENT VALIDATION
       ========================= */

    /**
     * Validates post title (5-150 characters).
     */
    public static boolean isValidTitle(String title) {
        return isNotBlank(title) && title.length() >= 5 && title.length() <= 150;
    }

    /**
     * Validates post content (5-10,000 characters).
     */
    public static boolean isValidContent(String content) {
        return isNotBlank(content) && content.length() >= 5 && content.length() <= 10_000;
    }

    /**
     * Validates comment content (1-2,000 characters).
     */
    public static boolean isValidComment(String comment) {
        return isNotBlank(comment) && comment.length() >= 1 && comment.length() <= 2_000;
    }

    /* =========================
       REVIEW VALIDATION
       ========================= */

    /**
     * Rating must be between 1 and 5.
     */
    public static boolean isValidRating(int rating) {
        return rating >= 1 && rating <= 5;
    }

    /* =========================
       TAG VALIDATION
       ========================= */

    /**
     * Validates tag name (2-30 characters, letters, numbers, underscore, hyphen).
     */
    public static boolean isValidTagName(String name) {
        return isNotBlank(name) && TAG_NAME_PATTERN.matcher(name).matches();
    }

    /* =========================
       HELPER METHODS
       ========================= */

    /**
     * Checks if a string is null or empty after trimming whitespace.
     */
    public static boolean isNotBlank(String value) {
        return value != null && !value.trim().isEmpty();
    }
    
    /**
     * A utility method to throw a UserInputsException if a validation condition is not met.
     * @param condition The boolean result of a validation check.
     * @param errorMessage The message for the exception if the condition is false.
     * @throws UserInputsException if the condition is false.
     */
    public static void validateAndThrow(boolean condition, String errorMessage) throws UserInputsException {
        if (!condition) {
            throw new UserInputsException(errorMessage);
        }
    }
}