package com.sbiz.cache.utils;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CacheEntry<K, V extends Serializable> {

    private K key;
    private boolean diskStored;
    private StoreManager<K, V> manager;

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Each cached entry has a key and the information regarding where is stored (memory or disk).
     * The manager will also decide where to store the value.
     */
    public CacheEntry(K key, V value, StoreManager<K, V> manager) {
        this.key = key;
        this.manager = manager;
        diskStored = manager.put(key, value);
    }

    public void updateValue(V value) {
        manager.updateValue(key, value, diskStored);
    }

    /**
     * Load the value for this cache entry from memory/disk
     */
    public V getValue() {
        return manager.getValue(key, diskStored);
    }

    /**
     * Method for moving this cache entry between memory and disk
     */
    public boolean switchStore() {
        diskStored = manager.switchStore(key, diskStored);
        return diskStored;
    }

    public V removeFromStore() {
        logger.debug("[{}] - removing from store", key);
        return manager.remove(key, diskStored);
    }

    public boolean isDiskStored() {
        return diskStored;
    }

}