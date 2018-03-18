package com.sbiz.cache.implementations;

import com.sbiz.cache.Cache;
import com.sbiz.cache.CacheDefaults;

public abstract class ACache<K, V> implements Cache<K, V>, CacheDefaults {

    //is disk caching enabled?
    private boolean diskEnabled = DEFAULT_DISK_ENABLED;
    
    //disk location of the cache
    private String diskLocation = DEFAULT_DISK_LOCATION;

    //Max Size of the cache. Defaults to 100;
    private int maxSize = DEFAULT_MAX_SIZE;

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

}
    