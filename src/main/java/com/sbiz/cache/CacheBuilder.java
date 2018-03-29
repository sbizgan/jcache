package com.sbiz.cache;

/**
 * Class for setting up a cache builder
 */
public class CacheBuilder implements CacheDefaults {

    private boolean diskEnabled = DEFAULT_DISK_ENABLED;
    private String diskLocation = DEFAULT_DISK_LOCATION;
    private int maxDiskSize = DEFAULT_MAX_SIZE_DISK;
    private int maxMemorySize = DEFAULT_MAX_SIZE_DISK;
    private boolean updateExisting = DEFAULT_UPDATE_EXISTING;
    private boolean printInternalsDebug = DEFAULT_PRINT_INTERNALS_DEBUG;

    //By default disk caching is disabled. Use this method to enable.
    public CacheBuilder enableDiskCaching() {
        this.diskEnabled = true;
        return this;
    }

    //This by default will enable diskCaching
    public CacheBuilder diskLocation(String diskLocation) {
        this.diskEnabled = true;
        this.diskLocation = diskLocation;
        return this;
    }

    /**
     * Set the maximum number of objects stored on disk. This will enable disk caching.
     */
    public CacheBuilder diskSize(int size) {
        if (size < 1)
            throw new IllegalArgumentException("Max size for disk cannot be less than 1!");
        this.maxDiskSize = size;
        this.diskEnabled = true;
        return this;
    }

    /**
     * Set the maxim number of objects stored in memory
     */
    public CacheBuilder memorySize(int size) {
        if (size < 1)
            throw new IllegalArgumentException("Max size for memory cannot be less than 1!");
        this.maxMemorySize = size;
        return this;
    }

    /**
     * Update the values when accessing put() with keys alread in the cache
     * @param updateExisting 
     */
    public CacheBuilder updateExisting(boolean updateExisting) {
        this.updateExisting = updateExisting;
        return this;
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
	 * @return the maxDiskSize
	 */
	public int getMaxDiskSize() {
		return maxDiskSize;
	}

    /**
	 * @return the maxMemorySize
	 */
	public int getMaxMemorySize() {
		return maxMemorySize;
	}
	public boolean isUpdateExisting() {
		return updateExisting;
	}

	/**
	 * @return the printInternalsDebug
	 */
	public boolean isPrintInternalsDebug() {
		return printInternalsDebug;
	}


	/**
	 * @return the printInternalsDebug
	 */
	public CacheBuilder printInternalsInDebug(boolean printInternalsInDebug) {
        this.printInternalsDebug = printInternalsInDebug;
		return this;
	}

    
}