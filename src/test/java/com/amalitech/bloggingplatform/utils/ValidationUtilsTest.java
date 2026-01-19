package com.amalitech.bloggingplatform.utils;

import com.amalitech.bloggingplatform.utils.exceptions.UserInputsException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValidationUtilsTest {

    @Test
    void isEmailValid() {
        assertTrue(ValidationUtils.isEmailValid("test@example.com"));
        assertTrue(ValidationUtils.isEmailValid("test.name@example.co.uk"));
        assertFalse(ValidationUtils.isEmailValid("test@example"));
        assertFalse(ValidationUtils.isEmailValid("test@.com"));
        assertFalse(ValidationUtils.isEmailValid(""));
        assertFalse(ValidationUtils.isEmailValid(null));
    }

    @Test
    void isPasswordValid() {
        assertTrue(ValidationUtils.isPasswordValid("Password@1"));
        assertFalse(ValidationUtils.isPasswordValid("password"));
        assertFalse(ValidationUtils.isPasswordValid("PASSWORD"));
        assertFalse(ValidationUtils.isPasswordValid("12345678"));
        assertFalse(ValidationUtils.isPasswordValid("short"));
        assertFalse(ValidationUtils.isPasswordValid(""));
        assertFalse(ValidationUtils.isPasswordValid(null));
    }

    @Test
    void isUsernameValid() {
        assertTrue(ValidationUtils.isUsernameValid("username123"));
        assertTrue(ValidationUtils.isUsernameValid("user-name"));
        assertTrue(ValidationUtils.isUsernameValid("user_name"));
        assertFalse(ValidationUtils.isUsernameValid("us"));
        assertFalse(ValidationUtils.isUsernameValid("user name"));
        assertFalse(ValidationUtils.isUsernameValid(""));
        assertFalse(ValidationUtils.isUsernameValid(null));
    }

    @Test
    void isValidObjectId() {
        assertTrue(ValidationUtils.isValidObjectId("60d5ec49e9e3c42c1c72a3b4"));
        assertFalse(ValidationUtils.isValidObjectId("invalid-id"));
        assertFalse(ValidationUtils.isValidObjectId(""));
        assertFalse(ValidationUtils.isValidObjectId(null));
    }

    @Test
    void isValidTitle() {
        assertTrue(ValidationUtils.isValidTitle("This is a valid title"));
        assertFalse(ValidationUtils.isValidTitle("sh"));
        assertFalse(ValidationUtils.isValidTitle(""));
        assertFalse(ValidationUtils.isValidTitle(null));
        String longTitle = "a".repeat(151);
        assertFalse(ValidationUtils.isValidTitle(longTitle));
    }

    @Test
    void isValidContent() {
        assertTrue(ValidationUtils.isValidContent("This is valid content."));
        assertFalse(ValidationUtils.isValidContent(""));
        assertFalse(ValidationUtils.isValidContent(null));
        assertTrue(ValidationUtils.isValidContent("short"));
        String longContent = "a".repeat(10001);
        assertFalse(ValidationUtils.isValidContent(longContent));
    }

    @Test
    void isValidComment() {
        assertTrue(ValidationUtils.isValidComment("This is a valid comment."));
        assertFalse(ValidationUtils.isValidComment(""));
        assertFalse(ValidationUtils.isValidComment(null));
        String longComment = "a".repeat(2001);
        assertFalse(ValidationUtils.isValidComment(longComment));
    }

    @Test
    void isValidRating() {
        assertTrue(ValidationUtils.isValidRating(1));
        assertTrue(ValidationUtils.isValidRating(5));
        assertFalse(ValidationUtils.isValidRating(0));
        assertFalse(ValidationUtils.isValidRating(6));
    }

    @Test
    void isValidTagName() {
        assertTrue(ValidationUtils.isValidTagName("java"));
        assertTrue(ValidationUtils.isValidTagName("web-dev"));
        assertFalse(ValidationUtils.isValidTagName("a"));
        assertFalse(ValidationUtils.isValidTagName(""));
        assertFalse(ValidationUtils.isValidTagName(null));
        String longTag = "a".repeat(31);
        assertFalse(ValidationUtils.isValidTagName(longTag));
    }

    @Test
    void isNotBlank() {
        assertTrue(ValidationUtils.isNotBlank("test"));
        assertFalse(ValidationUtils.isNotBlank(" "));
        assertFalse(ValidationUtils.isNotBlank(""));
        assertFalse(ValidationUtils.isNotBlank(null));
    }

    @Test
    void validateAndThrow_shouldThrowException_whenConditionIsFalse() {
        UserInputsException exception = assertThrows(UserInputsException.class, () -> {
            ValidationUtils.validateAndThrow(false, "Error message");
        });
        assertEquals("Error message", exception.getMessage());
    }

    @Test
    void validateAndThrow_shouldNotThrowException_whenConditionIsTrue() {
        assertDoesNotThrow(() -> {
            ValidationUtils.validateAndThrow(true, "Error message");
        });
    }
}
