package com.sbiz.cache;

import com.sbiz.cache.implementations.LRUCache;

import java.nio.file.FileSystems;

import com.sbiz.cache.CacheBuilder;

public class CacheTest {

    public static void main(String[] args) {
        LRUCache<String, String> cache = null;
		try {
			cache = new LRUCache<String, String>(new CacheBuilder().memorySize(3).diskSize(5).printInternalsInDebug(true));
		} catch (Exception e) {
			e.printStackTrace();
        }
        Object one = FileSystems.getDefault();

        String defaultdd = System.getProperty("user.home");
        cache.put("A", "Bim");
        cache.put("B", "Bam");
        cache.get("A");
        cache.put("C", "Bum");
        cache.put("D", "Badabum");
        cache.put("A", "Bim");
        cache.put("B", "Bam");
        cache.get("A");
        cache.put("C", "Bum");
        cache.put("D", "Badabum");
        cache.put("E", "Rapatam tap tap");
    }
}