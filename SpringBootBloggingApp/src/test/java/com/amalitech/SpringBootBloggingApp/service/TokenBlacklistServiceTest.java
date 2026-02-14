package com.amalitech.SpringBootBloggingApp.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TokenBlacklistServiceTest {

    private TokenBlacklistService tokenBlacklistService;

    @BeforeEach
    void setUp() {
        tokenBlacklistService = new TokenBlacklistService();
    }

    @Test
    void testBlacklistToken() {
        String token = "test-token-123";
        long expirationTime = System.currentTimeMillis() + 60000; // 1 minute from now

        tokenBlacklistService.blacklistToken(token, expirationTime);

        assertTrue(tokenBlacklistService.isTokenBlacklisted(token));
        assertEquals(1, tokenBlacklistService.getBlacklistedTokenCount());
    }

    @Test
    void testTokenNotBlacklisted() {
        String token = "non-blacklisted-token";

        assertFalse(tokenBlacklistService.isTokenBlacklisted(token));
    }

    @Test
    void testMultipleTokensBlacklisted() {
        String token1 = "token-1";
        String token2 = "token-2";
        long expirationTime = System.currentTimeMillis() + 60000;

        tokenBlacklistService.blacklistToken(token1, expirationTime);
        tokenBlacklistService.blacklistToken(token2, expirationTime);

        assertTrue(tokenBlacklistService.isTokenBlacklisted(token1));
        assertTrue(tokenBlacklistService.isTokenBlacklisted(token2));
        assertEquals(2, tokenBlacklistService.getBlacklistedTokenCount());
    }

    @Test
    void testCleanupExpiredTokens() throws InterruptedException {
        String expiredToken = "expired-token";
        String validToken = "valid-token";
        
        long pastTime = System.currentTimeMillis() - 1000; // Already expired
        long futureTime = System.currentTimeMillis() + 60000; // 1 minute from now

        tokenBlacklistService.blacklistToken(expiredToken, pastTime);
        tokenBlacklistService.blacklistToken(validToken, futureTime);

        assertEquals(2, tokenBlacklistService.getBlacklistedTokenCount());

        tokenBlacklistService.cleanupExpiredTokens();

        assertFalse(tokenBlacklistService.isTokenBlacklisted(expiredToken));
        assertTrue(tokenBlacklistService.isTokenBlacklisted(validToken));
        assertEquals(1, tokenBlacklistService.getBlacklistedTokenCount());
    }
}
