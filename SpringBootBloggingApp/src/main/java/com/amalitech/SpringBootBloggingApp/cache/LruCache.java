package com.amalitech.SpringBootBloggingApp.cache;

import java.util.LinkedHashMap;
import java.util.Map;

public class LruCache<K, V> implements Cache<K, V> {

    private static final int CAPACITY = 10;
    private static final long DEFAULT_TTL_MILLIS = 600000; // 10 minutes

    /**
     * Time-to-live for cache entries in milliseconds.
     */
    private final long ttlMillis;

    private final Map<K, CacheEntry<V>> cache;

    private static class CacheEntry<V> {
        private final V value;
        private final long expiryTimeMillis;

        CacheEntry(V value, long expiryTimeMillis) {
            this.value = value;
            this.expiryTimeMillis = expiryTimeMillis;
        }

        boolean isExpired() {
            return System.currentTimeMillis() > expiryTimeMillis;
        }

        V getValue() {
            return value;
        }
    }

    public LruCache() {
        this.ttlMillis = DEFAULT_TTL_MILLIS;
        this.cache = new LinkedHashMap<>(CAPACITY, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, CacheEntry<V>> eldest) {
                return size() > CAPACITY;
            }
        };
    }

    public LruCache(int capacity) {
        this.ttlMillis = DEFAULT_TTL_MILLIS;
        this.cache = new LinkedHashMap<>(capacity, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, CacheEntry<V>> eldest) {
                return size() > capacity;
            }
        };
    }

    public LruCache(int capacity, long ttlMillis) {
        this.ttlMillis = ttlMillis;
        this.cache = new LinkedHashMap<>(capacity, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, CacheEntry<V>> eldest) {
                return size() > capacity;
            }
        };
    }

    @Override
    public V get(K key) {
        CacheEntry<V> entry = cache.get(key);
        if (entry == null) {
            return null;
        }
        if (entry.isExpired()) {
            cache.remove(key);
            return null;
        }
        return entry.getValue();
    }

    @Override
    public void put(K key, V value) {
        long expiryTime = System.currentTimeMillis() + ttlMillis;
        cache.put(key, new CacheEntry<>(value, expiryTime));
    }

    @Override
    public void remove(K key) {
        cache.remove(key);
    }

    @Override
    public void clear() {
        cache.clear();
    }

    @Override
    public boolean containsKey(K key) {
        CacheEntry<V> entry = cache.get(key);
        if (entry == null) {
            return false;
        }
        if (entry.isExpired()) {
            cache.remove(key);
            return false;
        }
        return true;
    }
}


