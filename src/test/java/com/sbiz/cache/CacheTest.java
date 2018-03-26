package com.sbiz.cache;

import com.sbiz.cache.implementations.LRUCache;

import java.io.File;

import com.sbiz.cache.CacheBuilder;

public class CacheTest {

    public static void main(String[] args) {
        Cache<String, String> cache = null;
		try {
			cache = new LRUCache<String, String>(
                            new CacheBuilder()
                                    .diskLocation(
                                        System.getProperty("user.home") + 
                                        File.separator + ".jcache" + 
                                        File.separator + "testmain")
                                    .memorySize(3)
                                    .diskSize(5)
                                    .printInternalsInDebug(true));
		} catch (Exception e) {
			e.printStackTrace();
        }
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
        cache.get("D");
        cache.put("E", "Rapatam tap tap");
        cache.put("R", "Why so serious?");
    }
}