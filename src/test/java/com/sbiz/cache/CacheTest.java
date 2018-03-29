package com.sbiz.cache;

import com.sbiz.cache.implementations.LRUCache;
import com.sbiz.cache.CacheBuilder;
import com.sbiz.cache.Utils.RandomString;

import java.io.File;

public class CacheTest {


    // LOAD TEST
    public static void main(String[] args) {
        
        Cache<String, String> cache = new LRUCache<String, String>(
                            new CacheBuilder()
                                    .diskLocation(
                                        System.getProperty("user.home") + 
                                        File.separator + ".jcache" + 
                                        File.separator + "testmain")
                                    .memorySize(3)
                                    .diskSize(5)
                                    .printInternalsInDebug(true));

        //Generate keys using RandomString generator
        RandomString keysGenerator = new RandomString(10);
        RandomString valuesGenerator = new RandomString(100);

        keysGenerator.nextString();
        valuesGenerator.nextString();

        /** 
         * Span two threads: 
         *   one that will add keys/values (and adds keys to a reader)
         *   one that reads periodically random keys that were already added
         */
        

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