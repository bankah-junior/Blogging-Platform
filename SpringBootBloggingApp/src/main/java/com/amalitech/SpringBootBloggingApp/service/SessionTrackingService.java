package com.amalitech.SpringBootBloggingApp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Service
public class SessionTrackingService {

    private static final Logger logger = LoggerFactory.getLogger(SessionTrackingService.class);

    private final ConcurrentHashMap<String, SessionInfo> activeSessions = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Set<String>> userToSessions = new ConcurrentHashMap<>();

    public void createSession(String token, String username, String ipAddress) {
        SessionInfo sessionInfo = new SessionInfo(username, ipAddress, System.currentTimeMillis());
        activeSessions.put(token, sessionInfo);
        
        userToSessions.computeIfAbsent(username, k -> new CopyOnWriteArraySet<>()).add(token);
        
        logger.info("Session created for user: {} from IP: {}. Total active sessions: {}", 
                username, ipAddress, activeSessions.size());
    }

    public void invalidateSession(String token) {
        SessionInfo sessionInfo = activeSessions.remove(token);
        if (sessionInfo != null) {
            Set<String> userSessions = userToSessions.get(sessionInfo.getUsername());
            if (userSessions != null) {
                userSessions.remove(token);
                if (userSessions.isEmpty()) {
                    userToSessions.remove(sessionInfo.getUsername());
                }
            }
            logger.info("Session invalidated for user: {}. Total active sessions: {}", 
                    sessionInfo.getUsername(), activeSessions.size());
        }
    }

    public void invalidateAllUserSessions(String username) {
        Set<String> userSessions = userToSessions.get(username);
        if (userSessions != null) {
            int count = userSessions.size();
            userSessions.forEach(activeSessions::remove);
            userToSessions.remove(username);
            logger.info("Invalidated {} sessions for user: {}", count, username);
        }
    }

    public boolean isSessionActive(String token) {
        return activeSessions.containsKey(token);
    }

    public SessionInfo getSessionInfo(String token) {
        return activeSessions.get(token);
    }

    public int getActiveSessionCount() {
        return activeSessions.size();
    }

    public int getUserSessionCount(String username) {
        Set<String> sessions = userToSessions.get(username);
        return sessions != null ? sessions.size() : 0;
    }

    public Map<String, SessionInfo> getAllActiveSessions() {
        return new ConcurrentHashMap<>(activeSessions);
    }

    public static class SessionInfo {
        private final String username;
        private final String ipAddress;
        private final long createdAt;

        public SessionInfo(String username, String ipAddress, long createdAt) {
            this.username = username;
            this.ipAddress = ipAddress;
            this.createdAt = createdAt;
        }

        public String getUsername() {
            return username;
        }

        public String getIpAddress() {
            return ipAddress;
        }

        public long getCreatedAt() {
            return createdAt;
        }
    }
}
