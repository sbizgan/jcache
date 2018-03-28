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
            new LRUCache<String, String>();
        } catch (Exception e) {
            e.printStackTrace();
            fail("Could not create class: " + e.getMessage());
        }
        assert(true);
    }

    @Test
    @DisplayName("Test LRUCache with size smaller than 1")
    void smallSizeTest() {
        try {
            new LRUCache<String, String>(new CacheBuilder().memorySize(0));
        } catch (Exception e) {
            assert(true);
            return;
        }
        fail("LRUCache created with MaxSize < 1");
    }

    @Test
    @DisplayName("Test LRUCache with size 1")
    void sizeOneTest() {
        Cache<String, String> testCache = null;
        try {
            testCache = new LRUCache<String, String>(new CacheBuilder().memorySize(1));
        } catch (Exception e) {
            fail("Failed to create cache", e);
        }
        testCache.put("One", "value");
        testCache.put("Second", "value");
        assertTrue(testCache.size() == 1);
    }

    @Test
    @DisplayName("Basic test with memory cache only")
    void basicTest() {
        LRUCache<String, String> cache = null;
		try {
			cache = new LRUCache<String, String>(new CacheBuilder().memorySize(3).printInternalsInDebug(true));
		} catch (Exception e) {
			fail(e.getMessage());
        }

        cache.put("A", "Bim");
        cache.put("B", "Bam");
        cache.get("A");
        cache.put("C", "Bum");
        cache.put("D", "Badabum");
        assertTrue(true);
    }

    @Test
    void basicTwoLevelTest() {
        LRUCache<String, String> cache = new LRUCache<String, String>(
                    new CacheBuilder()
                            .memorySize(3)
                            .diskSize(2)
                            .printInternalsInDebug(true));
        
        cache.put("A", "Bim");
        cache.put("B", "Bam");
        cache.get("A");
        cache.put("C", "Bum");
        cache.put("D", "Badabum");
        cache.put("E", "Rapatam tap tap");
        cache.get("A");
        cache.put("F", "This is a test");
        cache.put("G", "This is a test");
        cache.get("G");
        cache.get("E");
        cache.put("H", "This is a test");
        cache.put("I", "This is a test");
        assertTrue(true);
    }
}