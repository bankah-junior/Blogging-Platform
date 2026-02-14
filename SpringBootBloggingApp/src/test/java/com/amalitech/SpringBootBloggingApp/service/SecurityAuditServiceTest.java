package com.amalitech.SpringBootBloggingApp.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SecurityAuditServiceTest {

    private SecurityAuditService securityAuditService;

    @BeforeEach
    void setUp() {
        securityAuditService = new SecurityAuditService();
    }

    @Test
    void testRecordSuccessfulLogin() {
        String username = "testuser";
        String email = "test@example.com";
        String ipAddress = "192.168.1.1";

        securityAuditService.recordLoginAttempt(username, email, ipAddress, true, "Login successful");

        Map<String, Object> report = securityAuditService.getSecurityReport();
        assertEquals(1L, report.get("successfulLogins"));
        assertEquals(0L, report.get("failedLogins"));
    }

    @Test
    void testRecordFailedLogin() {
        String username = "testuser";
        String email = "test@example.com";
        String ipAddress = "192.168.1.1";

        securityAuditService.recordLoginAttempt(username, email, ipAddress, false, "Invalid credentials");

        Map<String, Object> report = securityAuditService.getSecurityReport();
        assertEquals(0L, report.get("successfulLogins"));
        assertEquals(1L, report.get("failedLogins"));
    }

    @Test
    void testBruteForceDetection() {
        String email = "victim@example.com";
        String ipAddress = "192.168.1.100";

        // Simulate 5 failed login attempts
        for (int i = 0; i < 5; i++) {
            securityAuditService.recordLoginAttempt("victim", email, ipAddress, false, "Invalid password");
        }

        assertTrue(securityAuditService.isAccountLocked(email));
    }

    @Test
    void testAccountNotLockedWithFewerAttempts() {
        String email = "user@example.com";
        String ipAddress = "192.168.1.1";

        // Simulate 3 failed login attempts (below threshold)
        for (int i = 0; i < 3; i++) {
            securityAuditService.recordLoginAttempt("user", email, ipAddress, false, "Invalid password");
        }

        assertFalse(securityAuditService.isAccountLocked(email));
    }

    @Test
    void testSuccessfulLoginClearsFailedAttempts() {
        String email = "user@example.com";
        String ipAddress = "192.168.1.1";

        // Simulate 3 failed attempts followed by a successful login
        for (int i = 0; i < 3; i++) {
            securityAuditService.recordLoginAttempt("user", email, ipAddress, false, "Invalid password");
        }

        securityAuditService.recordLoginAttempt("user", email, ipAddress, true, "Login successful");

        assertFalse(securityAuditService.isAccountLocked(email));

        Map<String, Object> report = securityAuditService.getSecurityReport();
        assertEquals(1L, report.get("successfulLogins"));
    }

    @Test
    void testRecordTokenBlacklist() {
        String username = "testuser";
        String token = "test-token-123";

        securityAuditService.recordTokenBlacklist(username, token);

        assertEquals(1, securityAuditService.getRecentSecurityEvents(10).size());
    }

    @Test
    void testRecordSessionEvents() {
        String username = "testuser";
        String ipAddress = "192.168.1.1";

        securityAuditService.recordSessionCreated(username, ipAddress);
        securityAuditService.recordSessionTerminated(username, ipAddress);

        assertEquals(2, securityAuditService.getRecentSecurityEvents(10).size());
    }

    @Test
    void testGetSecurityReport() {
        String email = "test@example.com";
        String ipAddress = "192.168.1.1";

        securityAuditService.recordLoginAttempt("user1", email, ipAddress, true, "Login successful");
        securityAuditService.recordLoginAttempt("user2", "test2@example.com", ipAddress, false, "Invalid password");

        Map<String, Object> report = securityAuditService.getSecurityReport();

        assertNotNull(report);
        assertEquals(2L, report.get("totalLoginAttempts"));
        assertEquals(1L, report.get("successfulLogins"));
        assertEquals(1L, report.get("failedLogins"));
        assertTrue(report.containsKey("eventsByType"));
        assertTrue(report.containsKey("ipAddressFrequency"));
    }
}
