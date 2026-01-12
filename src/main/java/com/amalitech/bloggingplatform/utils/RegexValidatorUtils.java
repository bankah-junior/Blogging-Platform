package com.amalitech.bloggingplatform.utils;

import org.bson.types.ObjectId;

public final class RegexValidatorUtils {

    private RegexValidatorUtils() {
        // Utility class – prevent instantiation
    }

    /* =========================
       REGEX PATTERNS
       ========================= */

    private static final String EMAIL_REGEX =
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

    private static final String PASSWORD_REGEX =
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{4,}$";

    private static final String USERNAME_REGEX =
            "^[a-zA-Z0-9._-]{3,60}$";

    private static final String TAG_NAME_REGEX =
            "^[a-zA-Z0-9_-]{2,30}$";

    /* =========================
       USER VALIDATION
       ========================= */

    /**
     * Validates email format.
     */
    public static boolean isValidEmail(String email) {

        return isNotBlank(email) && email.matches(EMAIL_REGEX);
    }

    /**
     * Validates password strength:
     * - Min 8 characters
     * - Uppercase, lowercase, digit, special character
     */
    public static boolean isValidPassword(String password) {
        return isNotBlank(password) && password.matches(PASSWORD_REGEX);
    }

    /**
     * Username must be 3–60 chars, letters, numbers, dot, underscore, hyphen.
     */
    public static boolean isValidUsername(String username) {
        return isNotBlank(username) && username.matches(USERNAME_REGEX);
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
     * Validates post title.
     */
    public static boolean isValidTitle(String title) {
        return isNotBlank(title) && title.length() >= 5 && title.length() <= 150;
    }

    /**
     * Validates post or comment content.
     */
    public static boolean isValidContent(String content) {
        return isNotBlank(content) && content.length() >= 5 && content.length() <= 10_000;
    }

    /**
     * Validates comment content.
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
     * Validates tag name.
     */
    public static boolean isValidTagName(String name) {
        return isNotBlank(name) && name.matches(TAG_NAME_REGEX);
    }

    /* =========================
       HELPER METHODS
       ========================= */

    private static boolean isNotBlank(String value) {
        return value != null && !value.trim().isEmpty();
    }
}

