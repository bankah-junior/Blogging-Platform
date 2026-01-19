package com.amalitech.bloggingplatform.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordUtilsTest {

    @Test
    void hashPassword() {
        String password = "password123";
        String hashedPassword = PasswordUtils.hashPassword(password);
        assertNotNull(hashedPassword);
        assertNotEquals(password, hashedPassword);
    }

    @Test
    void verifyPassword() {
        String password = "password123";
        String hashedPassword = PasswordUtils.hashPassword(password);
        assertTrue(PasswordUtils.verifyPassword(password, hashedPassword));
        assertFalse(PasswordUtils.verifyPassword("wrongpassword", hashedPassword));
    }
}
