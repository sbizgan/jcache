package com.sbiz.cache.implementations;

import com.sbiz.cache.Cache;
import com.sbiz.cache.CacheDefaults;

public abstract class ACache<K, V> implements Cache<K, V>, CacheDefaults {



	// Is disk caching enabled?
    private boolean diskEnabled = DEFAULT_DISK_ENABLED;
    
    // Disk location of the cache
    private String diskLocation = DEFAULT_DISK_LOCATION;

    // Max Size of the cache
    private int maxSize = DEFAULT_MAX_SIZE;

    // If key exists should put() method update the value?
    private boolean updateExisting = DEFAULT_UPDATE_EXISTING;

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

	/**
	 * @return the maxSize
	 */
	public int getMaxSize() {
		return maxSize;
	}

	/**
	 * @param maxSize the maxSize to set
	 */
	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	}

	/**
	 * @return the updateExisting
	 */
	public boolean isUpdateExisting() {
		return updateExisting;
	}

	/**
	 * @param updateExisting the updateExisting to set
	 */
	public void setUpdateExisting(boolean updateExisting) {
		this.updateExisting = updateExisting;
	}

}
    