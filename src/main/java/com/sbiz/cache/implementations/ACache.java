package com.sbiz.cache.implementations;

import com.sbiz.cache.Cache;
import com.sbiz.cache.CacheBuilder;
import com.sbiz.cache.CacheDefaults;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ACache<K, V> implements Cache<K, V>, CacheDefaults {

    Logger logger = LoggerFactory.getLogger(ACache.class);


	private String cacheStrategy;

	private boolean printInternalsDebug = DEFAULT_PRINT_INTERNALS_DEBUG;

	// Is disk caching enabled?
    private boolean diskEnabled = DEFAULT_DISK_ENABLED;
    
    // Disk location of the cache
    private String diskLocation = DEFAULT_DISK_LOCATION;

    // Max Size of the cache
    private int maxSize = DEFAULT_MAX_SIZE;

    // If key exists should put() method update the value?
    private boolean updateExisting = DEFAULT_UPDATE_EXISTING;

	protected ACache() {

	}
	
	protected ACache(CacheBuilder builder) {
		logger.debug("ACache constructor with buidler");
        setDiskLocation(builder.getDiskLocation());
        setDiskEnabled(builder.isDiskEnabled());
        setMaxSize(builder.getMaxSize());
		setUpdateExisting(builder.isUpdateExisting());
		setPrintInternalsDebug(builder.isPrintInternalsDebug());
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

	/**
	 * @return the cacheStrategy
	 */
	public String getCacheStrategy() {
		return cacheStrategy;
	}

	/**
	 * @param cacheStrategy the cacheStrategy to set
	 */
	public void setCacheStrategy(String cacheStrategy) {
		this.cacheStrategy = cacheStrategy;
	}
		
    public String toString() {
		String fillRatio = String.format("% 4d",(int)((size() * 100.0f)/maxSize));
		StringBuilder sb = new StringBuilder("Cache ").append(cacheStrategy)
					.append("[").append(hashCode()).append("]")
					.append(" Fill ratio: [").append(fillRatio).append("]");
		if (isPrintInternalsDebug())
			sb.append(" | ").append(internals());
        return sb.toString();
	}

	public abstract String internals();

	/**
	 * @return the printInternalsDebug
	 */
	public boolean isPrintInternalsDebug() {
		return printInternalsDebug;
	}

	/**
	 * @param printInternalsDebug the printInternalsDebug to set
	 */
	public void setPrintInternalsDebug(boolean printInternalsDebug) {
		this.printInternalsDebug = printInternalsDebug;
	}

}
    