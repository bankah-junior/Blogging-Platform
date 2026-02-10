package com.amalitech.SpringBootBloggingApp.config;

import com.amalitech.SpringBootBloggingApp.cache.Cache;
import com.amalitech.SpringBootBloggingApp.cache.LruCache;
import com.amalitech.SpringBootBloggingApp.model.entity.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheConfig {

    /**
     * Cache bean for User entities.
     * Used by UserServiceImpl for caching user lookups.
     */
    @Bean
    public Cache<String, User> userCache() {
        return new LruCache<>();
    }
}
