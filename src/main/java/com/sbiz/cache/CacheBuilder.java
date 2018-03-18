package com.sbiz.cache;

/**
 * Class for Cache creation
 */
public class CacheBuilder implements CacheDefaults {

    private boolean diskEnabled = DEFAULT_DISK_ENABLED;
    private String diskLocation = DEFAULT_DISK_LOCATION;
    private int maxSize = DEFAULT_MAX_SIZE;
    private boolean updateExisting = DEFAULT_UPDATE_EXISTING;

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
        if (size < 1)
            throw new Exception("Max size for cache cannot be less than 1!");
        this.maxSize = size;
        return this;
    }

    /**
     * Update the values when accessing put() with keys alread in the cache
     * @param updateExisting 
     */
    public CacheBuilder updateExisting(boolean updateExisting) {
        this.updateExisting = updateExisting;
    }

	/**
	 * @return the diskEnabled
	 */
	public boolean isDiskEnabled() {
		return diskEnabled;
	}

    /**
	 * @return the diskLocation
	 */
	public String getDiskLocation() {
		return diskLocation;
	}

    /**
	 * @return the maxSize
	 */
	public int getMaxSize() {
		return maxSize;
	}

	public boolean isUpdateExisting() {
		return updateExisting;
	}
    
}