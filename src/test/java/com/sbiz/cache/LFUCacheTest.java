package com.sbiz.cache;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.sbiz.cache.implementations.LFUCache;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LFUCacheTest {

    @Test
    @DisplayName("Test creating LFUCache instance")
    void simpleTest() {
        try {
            new LFUCache<String, String>();
        } catch (Exception e) {
            fail("Could not create class: " + e.getMessage());
        }
        assert(true);
    }

    @Test
    @DisplayName("Test LRUCache with size smaller than 1")
    void smallSizeTest() {
        try {
            new LFUCache<String, String>(new CacheBuilder().memorySize(0));
        } catch (Exception e) {
            assert(true);
            return;
        }
        fail("LRUCache created with MaxSize < 1");
    }

    @Test
    @DisplayName("Test LFUCache with size 1")
    void sizeOneTest() {
        Cache<String, String> testCache = null;
        try {
            testCache = new LFUCache<String, String>(new CacheBuilder().memorySize(1));
        } catch (Exception e) {
            fail("Failed to create cache", e);
        }
        testCache.put("One", "value");
        testCache.put("Second", "value");
        assertTrue(testCache.size() == 1);
    }

    @Test
    @DisplayName("Some basic tests")
    void basicTest() {
        LFUCache<String, String> cache = null;
		try {
			cache = new LFUCache<String, String>(new CacheBuilder().memorySize(3).printInternalsInDebug(true));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			fail(e.getMessage());
        }
        
        //TODO What will happen if we set Max size -1 or less than the size? test
        cache.put("A", "Bim");
        cache.put("B", "Bam");
        cache.get("A");
        cache.put("C", "Bum");
        cache.put("D", "Badabum");
        assertTrue(true);
    }

}