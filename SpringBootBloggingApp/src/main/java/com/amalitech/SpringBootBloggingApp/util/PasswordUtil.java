package com.amalitech.SpringBootBloggingApp.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {

    private static final int COST = 12;

    private PasswordUtil() {}

    /** Hash password
     * @param plainPassword The plain text password to hash
     * @return The hashed password
     */
    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(COST));
    }

    /** Verify password
     * @param plainPassword The plain text password to verify
     * @param hashedPassword The hashed password to compare against
     * @return true if the passwords match, false otherwise
     */
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}