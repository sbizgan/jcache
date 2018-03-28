package com.sbiz.cache.utils;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

import com.sbiz.cache.CacheDefaults;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StoreManager<K, V extends Serializable>  {

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    
    private ConcurrentHashMap<K, V> memoryStore;

    private DiskStore<K, V> fileStore;

    // Max disk size of the cache
    private int maxDiskSize = CacheDefaults.DEFAULT_MAX_SIZE_DISK;
    
    // Max disk size of the cache
    private int maxMemorySize = CacheDefaults.DEFAULT_MAX_SIZE_DISK;

    // Is disk caching enabled?
    private boolean diskEnabled = CacheDefaults.DEFAULT_DISK_ENABLED;
    
    public StoreManager() {
        memoryStore = new ConcurrentHashMap<K, V>();
        if (diskEnabled)
            fileStore = new DiskStore<K, V>();
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
        
        // see first if memory has space
        if (memoryStore.size() < maxMemorySize) {
            memoryStore.put(key, value);
            return false;
        } 
        
        // see if disk has space
        if (diskEnabled && fileStore.size() < maxMemorySize) { 
            fileStore.add(key, value);
            return true;
        }

        // Space should be available this point should not be reached
        logger.error("No space available to add entires. Something wen bad! Please report this bug!");
        throw new SecurityException("No room to add entries. Something went bad! Please report this bug!");

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

    public void setDiskLocation(String diskLocation) {
        if (fileStore == null)
            fileStore = new DiskStore<K, V>();
        fileStore.setDiskLocation(diskLocation);
	}

    public String toString() {
        String memoryFillRatio = String.format("%3d",(int)((memoryStore.size() * 100.0f)/maxMemorySize));
        String diskFillRatio = String.format("%3d",(int)((fileStore.size() * 100.0f)/maxDiskSize));
        String diskSize = String.format("%d MB",(int)(fileStore.getDiskSize()/1024));
        
        StringBuilder sb = new StringBuilder();
        
        sb.append("[Memory: ").append(memoryFillRatio).append("%]");
        if (diskEnabled) {
            sb.append(" [DiskObjects: ").append(diskFillRatio).append("%]");
            sb.append(" [DiskSize: ").append(diskSize).append("]");
        }
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

	public void build() {
        if (diskEnabled)
            fileStore.initLocation();
	}

	public boolean isMemoryFull() {
		return memoryStore.size() == maxMemorySize;
	}

}