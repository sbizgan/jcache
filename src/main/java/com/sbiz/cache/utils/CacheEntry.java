package com.sbiz.cache.utils;

import java.io.Serializable;

public class CacheEntry<K, V extends Serializable> {

    private K key;
    private boolean diskStored;
    private String subFolder;
    private StoreManager<K, V> manager;

    /**
     * Each cached entry has a key and the information regarding where is stored (memory or disk).
     * The manager will also decide where to store the value.
     */
    public CacheEntry(K key, V value, StoreManager<K, V> manager) {
        this.key = key;
        this.manager = manager;
        
        // Generate subfolder key only if cache has second level (disk) enabled
        if (manager.isDiskEnabled())
            this.subFolder = DiskStore.getNextSubFolder();

        this.diskStored = manager.put(this, value);
    }

    public String getSubFolder() {
        return subFolder;
    }

    public void updateValue(V value) {
        manager.updateValue(this, value);
    }

    public K getKey() {
        return key;
    }

    /**
     * Load the value for this cache entry from memory/disk
     */
    public V getValue() {
        return manager.getValue(this);//key, diskStored);
    }

    /**
     * Method for moving this cache entry between memory and disk
     */
    public boolean switchStore() {
        diskStored = manager.switchStore(this);
        return diskStored;
    }

    public V removeFromStore() {
        return manager.remove(this);
    }

    public boolean isDiskStored() {
        return diskStored;
    }

}