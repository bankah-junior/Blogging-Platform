package com.amalitech.SpringBootBloggingApp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class SecurityAuditService {

    private static final Logger logger = LoggerFactory.getLogger(SecurityAuditService.class);
    private static final int MAX_LOGIN_ATTEMPTS = 5;
    private static final long LOCKOUT_DURATION_MS = 900000; // 15 minutes

    private final ConcurrentHashMap<String, List<LoginAttempt>> loginAttempts = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> lockedAccounts = new ConcurrentHashMap<>();
    private final List<SecurityEvent> securityEvents = Collections.synchronizedList(new ArrayList<>());

    public void recordLoginAttempt(String username, String email, String ipAddress, boolean success, String reason) {
        LoginAttempt attempt = new LoginAttempt(username, email, ipAddress, System.currentTimeMillis(), success, reason);
        
        loginAttempts.computeIfAbsent(email, k -> Collections.synchronizedList(new ArrayList<>())).add(attempt);
        
        SecurityEvent event = new SecurityEvent(
                success ? SecurityEventType.LOGIN_SUCCESS : SecurityEventType.LOGIN_FAILURE,
                username,
                email,
                ipAddress,
                System.currentTimeMillis(),
                reason
        );
        securityEvents.add(event);

        if (success) {
            logger.info("Successful login - User: {}, Email: {}, IP: {}", username, email, ipAddress);
            clearFailedAttempts(email);
        } else {
            logger.warn("Failed login attempt - Email: {}, IP: {}, Reason: {}", email, ipAddress, reason);
            checkBruteForce(email);
        }
    }

    public void recordTokenBlacklist(String username, String token) {
        SecurityEvent event = new SecurityEvent(
                SecurityEventType.TOKEN_BLACKLISTED,
                username,
                null,
                null,
                System.currentTimeMillis(),
                "Token blacklisted on logout"
        );
        securityEvents.add(event);
        logger.info("Token blacklisted for user: {}", username);
    }

    public void recordSessionCreated(String username, String ipAddress) {
        SecurityEvent event = new SecurityEvent(
                SecurityEventType.SESSION_CREATED,
                username,
                null,
                ipAddress,
                System.currentTimeMillis(),
                "New session created"
        );
        securityEvents.add(event);
        logger.info("Session created - User: {}, IP: {}", username, ipAddress);
    }

    public void recordSessionTerminated(String username, String ipAddress) {
        SecurityEvent event = new SecurityEvent(
                SecurityEventType.SESSION_TERMINATED,
                username,
                null,
                ipAddress,
                System.currentTimeMillis(),
                "Session terminated on logout"
        );
        securityEvents.add(event);
        logger.info("Session terminated - User: {}, IP: {}", username, ipAddress);
    }

    private void checkBruteForce(String email) {
        List<LoginAttempt> attempts = loginAttempts.get(email);
        if (attempts == null) return;

        long currentTime = System.currentTimeMillis();
        long recentFailures = attempts.stream()
                .filter(a -> !a.isSuccess())
                .filter(a -> (currentTime - a.getTimestamp()) < 600000) // Last 10 minutes
                .count();

        if (recentFailures >= MAX_LOGIN_ATTEMPTS) {
            lockedAccounts.put(email, currentTime + LOCKOUT_DURATION_MS);
            logger.error("SECURITY ALERT: Potential brute-force attack detected for email: {}. Account locked for 15 minutes.", email);
            
            SecurityEvent event = new SecurityEvent(
                    SecurityEventType.BRUTE_FORCE_DETECTED,
                    null,
                    email,
                    null,
                    currentTime,
                    String.format("Account locked after %d failed attempts", recentFailures)
            );
            securityEvents.add(event);
        }
    }

    public boolean isAccountLocked(String email) {
        Long lockoutTime = lockedAccounts.get(email);
        if (lockoutTime == null) return false;

        if (System.currentTimeMillis() < lockoutTime) {
            return true;
        } else {
            lockedAccounts.remove(email);
            return false;
        }
    }

    private void clearFailedAttempts(String email) {
        List<LoginAttempt> attempts = loginAttempts.get(email);
        if (attempts != null) {
            attempts.removeIf(a -> !a.isSuccess());
        }
        lockedAccounts.remove(email);
    }

    public Map<String, Object> getSecurityReport() {
        Map<String, Object> report = new HashMap<>();
        
        long totalAttempts = loginAttempts.values().stream()
                .mapToLong(List::size)
                .sum();
        
        long failedAttempts = loginAttempts.values().stream()
                .flatMap(List::stream)
                .filter(a -> !a.isSuccess())
                .count();
        
        long successfulAttempts = totalAttempts - failedAttempts;
        
        report.put("totalLoginAttempts", totalAttempts);
        report.put("successfulLogins", successfulAttempts);
        report.put("failedLogins", failedAttempts);
        report.put("lockedAccounts", lockedAccounts.size());
        report.put("totalSecurityEvents", securityEvents.size());
        
        Map<SecurityEventType, Long> eventsByType = securityEvents.stream()
                .collect(Collectors.groupingBy(SecurityEvent::getType, Collectors.counting()));
        report.put("eventsByType", eventsByType);
        
        List<LoginAttempt> recentFailures = loginAttempts.values().stream()
                .flatMap(List::stream)
                .filter(a -> !a.isSuccess())
                .filter(a -> (System.currentTimeMillis() - a.getTimestamp()) < 3600000) // Last hour
                .sorted(Comparator.comparing(LoginAttempt::getTimestamp).reversed())
                .limit(10)
                .collect(Collectors.toList());
        report.put("recentFailures", recentFailures);
        
        Map<String, Long> ipAddressFrequency = loginAttempts.values().stream()
                .flatMap(List::stream)
                .collect(Collectors.groupingBy(LoginAttempt::getIpAddress, Collectors.counting()));
        report.put("ipAddressFrequency", ipAddressFrequency);
        
        return report;
    }

    public List<SecurityEvent> getRecentSecurityEvents(int limit) {
        return securityEvents.stream()
                .sorted(Comparator.comparing(SecurityEvent::getTimestamp).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    public Map<String, List<LoginAttempt>> getLoginAttemptsByEmail(String email) {
        Map<String, List<LoginAttempt>> result = new HashMap<>();
        result.put(email, loginAttempts.getOrDefault(email, Collections.emptyList()));
        return result;
    }

    public static class LoginAttempt {
        private final String username;
        private final String email;
        private final String ipAddress;
        private final long timestamp;
        private final boolean success;
        private final String reason;

        public LoginAttempt(String username, String email, String ipAddress, long timestamp, boolean success, String reason) {
            this.username = username;
            this.email = email;
            this.ipAddress = ipAddress;
            this.timestamp = timestamp;
            this.success = success;
            this.reason = reason;
        }

        public String getUsername() { return username; }
        public String getEmail() { return email; }
        public String getIpAddress() { return ipAddress; }
        public long getTimestamp() { return timestamp; }
        public boolean isSuccess() { return success; }
        public String getReason() { return reason; }
    }

    public static class SecurityEvent {
        private final SecurityEventType type;
        private final String username;
        private final String email;
        private final String ipAddress;
        private final long timestamp;
        private final String details;

        public SecurityEvent(SecurityEventType type, String username, String email, String ipAddress, long timestamp, String details) {
            this.type = type;
            this.username = username;
            this.email = email;
            this.ipAddress = ipAddress;
            this.timestamp = timestamp;
            this.details = details;
        }

        public SecurityEventType getType() { return type; }
        public String getUsername() { return username; }
        public String getEmail() { return email; }
        public String getIpAddress() { return ipAddress; }
        public long getTimestamp() { return timestamp; }
        public String getDetails() { return details; }
    }

    public enum SecurityEventType {
        LOGIN_SUCCESS,
        LOGIN_FAILURE,
        TOKEN_BLACKLISTED,
        SESSION_CREATED,
        SESSION_TERMINATED,
        BRUTE_FORCE_DETECTED
    }
}
