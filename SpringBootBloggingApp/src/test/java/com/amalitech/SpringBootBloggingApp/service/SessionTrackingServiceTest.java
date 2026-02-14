package com.amalitech.SpringBootBloggingApp.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SessionTrackingServiceTest {

    private SessionTrackingService sessionTrackingService;

    @BeforeEach
    void setUp() {
        sessionTrackingService = new SessionTrackingService();
    }

    @Test
    void testCreateSession() {
        String token = "test-token-123";
        String username = "testuser";
        String ipAddress = "192.168.1.1";

        sessionTrackingService.createSession(token, username, ipAddress);

        assertTrue(sessionTrackingService.isSessionActive(token));
        assertEquals(1, sessionTrackingService.getActiveSessionCount());
        assertEquals(1, sessionTrackingService.getUserSessionCount(username));
    }

    @Test
    void testGetSessionInfo() {
        String token = "test-token-456";
        String username = "john";
        String ipAddress = "10.0.0.1";

        sessionTrackingService.createSession(token, username, ipAddress);

        SessionTrackingService.SessionInfo sessionInfo = sessionTrackingService.getSessionInfo(token);

        assertNotNull(sessionInfo);
        assertEquals(username, sessionInfo.getUsername());
        assertEquals(ipAddress, sessionInfo.getIpAddress());
        assertTrue(sessionInfo.getCreatedAt() > 0);
    }

    @Test
    void testInvalidateSession() {
        String token = "token-to-invalidate";
        String username = "testuser";
        String ipAddress = "192.168.1.1";

        sessionTrackingService.createSession(token, username, ipAddress);
        assertTrue(sessionTrackingService.isSessionActive(token));

        sessionTrackingService.invalidateSession(token);

        assertFalse(sessionTrackingService.isSessionActive(token));
        assertEquals(0, sessionTrackingService.getActiveSessionCount());
        assertEquals(0, sessionTrackingService.getUserSessionCount(username));
    }

    @Test
    void testMultipleSessionsPerUser() {
        String username = "multiuser";
        String token1 = "token-1";
        String token2 = "token-2";
        String ipAddress = "192.168.1.1";

        sessionTrackingService.createSession(token1, username, ipAddress);
        sessionTrackingService.createSession(token2, username, ipAddress);

        assertEquals(2, sessionTrackingService.getUserSessionCount(username));
        assertTrue(sessionTrackingService.isSessionActive(token1));
        assertTrue(sessionTrackingService.isSessionActive(token2));
    }

    @Test
    void testInvalidateAllUserSessions() {
        String username = "testuser";
        String token1 = "token-1";
        String token2 = "token-2";
        String ipAddress = "192.168.1.1";

        sessionTrackingService.createSession(token1, username, ipAddress);
        sessionTrackingService.createSession(token2, username, ipAddress);

        assertEquals(2, sessionTrackingService.getUserSessionCount(username));

        sessionTrackingService.invalidateAllUserSessions(username);

        assertEquals(0, sessionTrackingService.getUserSessionCount(username));
        assertFalse(sessionTrackingService.isSessionActive(token1));
        assertFalse(sessionTrackingService.isSessionActive(token2));
    }

    @Test
    void testSessionNotActive() {
        String nonExistentToken = "non-existent-token";

        assertFalse(sessionTrackingService.isSessionActive(nonExistentToken));
        assertNull(sessionTrackingService.getSessionInfo(nonExistentToken));
    }
}
