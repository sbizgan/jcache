package com.sbiz.cache;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.sbiz.cache.implementations.LRUCache;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LRUCacheTest {

    @Test
    @DisplayName("Test creating LRUCache instance")
    void simpleTest() {
        try {
            new LRUCache();
        } catch (Exception e) {
            fail("Could not create class: " + e.getMessage());
        }
        assert(true);
    }

    @Test
    @DisplayName("Test LRUCache with size smaller than 1")
    void smallSizeTest() {
        Cache testCache = null;
        try {
            testCache = new LRUCache(new CacheBuilder().maxSize(0));
        } catch (Exception e) {
            assert(true);
            return;
        }
        fail("LRUCache created with MaxSize < 1");
    }

    @Test
    @DisplayName("Test LRUCache with size 1")
    void sizeOneTest() {
        Cache testCache = null;
        try {
            testCache = new LRUCache(new CacheBuilder().maxSize(1));
        } catch (Exception e) {
            fail("Failed to create cache", e);
        }
        testCache.put("One", "value");
        testCache.put("Second", "value");
        assertTrue(testCache.size() == 1);
    }

}