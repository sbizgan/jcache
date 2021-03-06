package com.sbiz.cache.implementations;

import java.io.Serializable;

import com.sbiz.cache.Cache;
import com.sbiz.cache.CacheBuilder;
import com.sbiz.cache.CacheDefaults;
import com.sbiz.cache.utils.StoreManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ACache<K, V extends Serializable> implements Cache<K, V>, CacheDefaults {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

	protected String cacheStrategy;

	// Used for debuging purposes
	private boolean printInternalsDebug = DEFAULT_PRINT_INTERNALS_DEBUG;

    // If key exists should put() method update the value?
    private boolean updateExisting = DEFAULT_UPDATE_EXISTING;

	protected int size;

	protected StoreManager<K, V> store;

	protected ACache() {
		size = 0;
		store = new StoreManager<K, V>();
		store.build();
		initializeStrategy();
	}

	protected ACache(CacheBuilder builder) {
		size = 0;
		store = new StoreManager<K, V>();
		initializeStrategy();
		store.setDiskLocation(builder.getDiskLocation());
        store.setDiskEnabled(builder.isDiskEnabled());
		store.setMaxDiskSize(builder.getMaxDiskSize());
		store.setMaxMemorySize(builder.getMaxMemorySize());
		store.setSubFoldersPattern(builder.getSubFoldersPattern());
		store.build();
		setUpdateExisting(builder.isUpdateExisting());
		setPrintInternalsDebug(builder.isPrintInternalsDebug());
	}

	public abstract String internals();
	
	protected abstract void initializeStrategy();

	public int size() {
		return size;
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
		StringBuilder sb = new StringBuilder().append("Cache [")
					.append(hashCode()).append("]");
        return sb.toString();
	}

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

	public synchronized void put(K key, V value) {
		if (isPrintInternalsDebug())
			logger.debug("  Strategy info: {}", internals());
	} 

	public int getMaxSize() {
		return store.getMaxMemorySize() + 
			(store.isDiskEnabled() ? store.getMaxDiskSize() : 0);
	}

}
    