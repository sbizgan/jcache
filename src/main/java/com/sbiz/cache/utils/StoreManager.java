package com.sbiz.cache.utils;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

import com.sbiz.cache.CacheDefaults;

public class StoreManager<K, V extends Serializable>  {

    private ConcurrentHashMap<K, V> memoryStore;
    private DiskStore<K, V> fileStore;


    // Max disk size of the cache
    private int maxDiskSize = CacheDefaults.DEFAULT_MAX_SIZE_DISK;
    
    // Max disk size of the cache
    private int maxMemorySize = CacheDefaults.DEFAULT_MAX_SIZE_DISK;

    // Is disk caching enabled?
    private boolean diskEnabled = CacheDefaults.DEFAULT_DISK_ENABLED;
    
    // Disk location of the cache
    private String diskLocation = CacheDefaults.DEFAULT_DISK_LOCATION;

    public StoreManager() {
        memoryStore = new ConcurrentHashMap<K, V>();
        fileStore = new DiskStore<K, V>(diskLocation);
    }

    /** 
     * Load the value from either memory/disk
     */
	public V getValue(K key, boolean diskStored) {
        if (diskStored)
            return fileStore.getValue(key);
        else
            return memoryStore.get(key);
	}

    /**
     * Method that will store this value depending on memory/disk load.<br><br>
     * Will return <code>true</code> if stored on disk or <code>false</code> if 
     * stored on memory.
     */
	public boolean put(K key, V value) {
        
        // see if memory has space
        if (memoryStore.size() < maxMemorySize) {
            memoryStore.put(key, value);
            return false;
        } 
        
        // see if disk has space
        if (fileStore.size() < maxMemorySize) { 
            fileStore.add(key, value);
            return true;
        }

        // no space! remove entry from 
        // TODO should we delete first? based on strategy?

        // add to file store
        //TODO evict object if 
        return true;         

	}

	public boolean switchStore(K key, boolean diskStored) {
        if (diskStored)
            return moveInMemory(key);
        else
            return moveToDisk(key);
	}

    /**
     * Move <code>value</code> corresponding to <code>key</code>
     * from Memory to Disk. This will return <code>true</code> as will be stored in diskStored variable
     */
	private boolean moveToDisk(K key) {
        V value = memoryStore.get(key);
        memoryStore.remove(key);
        fileStore.add(key, value);
        return true;
	}

    /**
     * Move <code>value</code> corresponding to <code>key</code>
     * from Disk to Memory. This will return <code>false</code> as will be stored in diskStored variable
     */
	private boolean moveInMemory(K key) {
        V value = fileStore.getValue(key);
        fileStore.remove(key);
        memoryStore.put(key, value);
        return false;
	}

    //TODO
	public void updateValue(K key, V value, boolean diskStored) {
        
	}

    //TODO
	public void clear() {
        
	}

	/**
	 * @return the maxDiskSize
	 */
	public int getMaxDiskSize() {
		return maxDiskSize;
	}

	/**
	 * @param maxSize the maxDiskSize to set
	 */
	public void setMaxDiskSize(int maxSize) {
		this.maxDiskSize = maxSize;
	}

	/**
	 * @return the maxMamorySize
	 */
	public int getMaxMemorySize() {
		return maxMemorySize;
	}

	/**
	 * @param maxSize the maxMamorySize to set
	 */
	public void setMaxMemorySize(int maxSize) {
		this.maxMemorySize = maxSize;
	}

	public boolean isDiskEnabled () {
        return diskEnabled;
    }
    
    public void setDiskEnabled(boolean diskEnabled) {
        this.diskEnabled = diskEnabled;
    }


    public String getDiskLocation () {
        return diskLocation;
    }

    public void setDiskLocation(String diskLocation) {
        this.diskLocation = diskLocation;
	}

    public String toString() {
        String memoryFillRatio = String.format("%3d",(int)((memoryStore.size() * 100.0f)/maxMemorySize));
        String diskFillRatio = String.format("%3d",(int)((fileStore.size() * 100.0f)/maxDiskSize));
        
        StringBuilder sb = new StringBuilder();
        
        sb.append("[M: ").append(memoryFillRatio).append("%]");
        if (diskEnabled)
            sb.append(" [D: ").append(diskFillRatio).append("%]");
        return sb.toString();
    }

	public V remove(K key, boolean diskStored) {
        V value = getValue(key, diskStored);
        if (diskStored)
            fileStore.remove(key);
        else
            memoryStore.remove(key);
        return value;
	}

}