package com.amalitech.SpringBootBloggingApp.config;

import com.amalitech.SpringBootBloggingApp.cache.Cache;
import com.amalitech.SpringBootBloggingApp.cache.LruCache;
import com.amalitech.SpringBootBloggingApp.model.entity.User;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Cache bean for User entities.
     * Used by UserServiceImpl for caching user lookups.
     */
    @Bean
    public Cache<String, User> userCache() {
        return new LruCache<>();
    }

    /**
     * Spring Cache Manager using Caffeine.
     * Configures cache behavior for all @Cacheable annotations.
     */
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(
            "posts", "comments", "reviews", "tags", "users"
        );
        cacheManager.setCaffeine(Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .recordStats());
        return cacheManager;
    }
}
