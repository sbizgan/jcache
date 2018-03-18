package com.sbiz.cache;

import com.sbiz.cache.implementations.LFUCache;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple CacheManager.
 */
public class CacheTest extends TestCase {
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public CacheTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(CacheTest.class);
    }

    /**
     * Rigourous Test :-)
     */
    public void testCache() {
        try {
			Cache myCache = new CacheBuilder()
                    .LRUCache()
                    .diskLocation("some location here test")
                    .create();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        assertTrue(true);
    }
}
