package com.amalitech.SpringBootBloggingApp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenBlacklistService {

    private static final Logger logger = LoggerFactory.getLogger(TokenBlacklistService.class);

    private final ConcurrentHashMap<String, Long> blacklistedTokens = new ConcurrentHashMap<>();

    public void blacklistToken(String token, long expirationTime) {
        blacklistedTokens.put(token, expirationTime);
        logger.info("Token blacklisted. Total blacklisted tokens: {}", blacklistedTokens.size());
    }

    public boolean isTokenBlacklisted(String token) {
        return blacklistedTokens.containsKey(token);
    }

    @Scheduled(fixedRate = 3600000) // Run every hour
    public void cleanupExpiredTokens() {
        long currentTime = System.currentTimeMillis();
        int sizeBefore = blacklistedTokens.size();
        
        blacklistedTokens.entrySet().removeIf(entry -> entry.getValue() < currentTime);
        
        int sizeAfter = blacklistedTokens.size();
        if (sizeBefore > sizeAfter) {
            logger.info("Cleaned up {} expired tokens from blacklist. Remaining: {}", 
                    sizeBefore - sizeAfter, sizeAfter);
        }
    }

    public int getBlacklistedTokenCount() {
        return blacklistedTokens.size();
    }
}
