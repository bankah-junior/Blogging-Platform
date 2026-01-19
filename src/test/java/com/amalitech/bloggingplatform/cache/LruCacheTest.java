package com.amalitech.bloggingplatform.cache;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LruCacheTest {

    private LruCache<String, String> cache;

    @BeforeEach
    void setUp() {
        cache = new LruCache<>(2);
    }

    @Test
    void get() {
        cache.put("1", "one");
        assertEquals("one", cache.get("1"));
        assertNull(cache.get("2"));
    }

    @Test
    void put() {
        cache.put("1", "one");
        cache.put("2", "two");
        assertEquals("one", cache.get("1"));
        assertEquals("two", cache.get("2"));
    }

    @Test
    void testEviction() {
        cache.put("1", "one");
        cache.put("2", "two");
        cache.put("3", "three"); // This should evict "1"
        assertNull(cache.get("1"));
        assertEquals("two", cache.get("2"));
        assertEquals("three", cache.get("3"));
    }
    
    @Test
    void testAccessOrder() {
        cache.put("1", "one");
        cache.put("2", "two");
        cache.get("1"); // Access "1", making "2" the least recently used
        cache.put("3", "three"); // This should evict "2"
        assertNull(cache.get("2"));
        assertEquals("one", cache.get("1"));
        assertEquals("three", cache.get("3"));
    }


    @Test
    void remove() {
        cache.put("1", "one");
        cache.remove("1");
        assertNull(cache.get("1"));
    }

    @Test
    void clear() {
        cache.put("1", "one");
        cache.put("2", "two");
        cache.clear();
        assertNull(cache.get("1"));
        assertNull(cache.get("2"));
    }

    @Test
    void containsKey() {
        cache.put("1", "one");
        assertTrue(cache.containsKey("1"));
        assertFalse(cache.containsKey("2"));
    }
}
