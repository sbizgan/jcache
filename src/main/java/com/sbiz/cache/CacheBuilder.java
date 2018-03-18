package com.sbiz.cache;

import com.sbiz.cache.implementations.ACache;
import com.sbiz.cache.implementations.LFUCache;
import com.sbiz.cache.implementations.LRUCache;

/**
 * Class for Cache creation
 */
public class CacheBuilder implements CacheDefaults {


    //default setting for disk caching is set to false;
    private boolean diskEnabled = false;

    //default caching location is...
    private String diskLocation;

    private int maxSize = DEFAULT_MAX_SIZE;
        
    private ACache cacheImplementation;

    public CacheBuilder LRUCache() {
        this.cacheImplementation = new LRUCache();
        return this;
    }

    public CacheBuilder LFUCache() {
        this.cacheImplementation = new LFUCache();
        return this;
    }

    //By default disk caching is disabled. Use this method to enable.
    public CacheBuilder enableDiskCaching() {
        this.diskEnabled = true;
        return this;
    }

    //This by default will enable diskCaching
    public CacheBuilder diskLocation(String diskLocation) throws Exception {
        this.diskEnabled = true;
        //TODO Add filepath verifications here
        this.diskLocation = diskLocation;
        return this;
    }

    public CacheBuilder maxSize(int size) throws Exception {
        this.maxSize = size;
        return this;
    }

    public Cache create() {
        cacheImplementation.setDiskLocation(diskLocation);
        cacheImplementation.setDiskEnabled(diskEnabled);
        cacheImplementation.setMaxSize(maxSize);
        return cacheImplementation;
    }

}