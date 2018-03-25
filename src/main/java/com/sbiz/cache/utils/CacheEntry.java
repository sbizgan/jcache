package com.sbiz.cache.utils;

public class CacheEntry<K, V> {

    private K key;
    private boolean diskStored;
    private StoreManager<K, V> manager;


    /**
     * Each cached entry has a key and the information regarding where is stored (memory or disk)
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
    public void switchStore() {
        manager.switchStore(key, diskStored);
    }

    public void removeFromStore() {
        manager.remove(key, diskStored);
    }

}