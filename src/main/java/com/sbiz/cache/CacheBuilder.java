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
    private String subFoldersPattern = DEFAULT_SUBFOLDERS_PATTERN;

    /**
     * By default disk caching is disabled. Use this method to enable it.
     */
    public CacheBuilder enableDiskCaching() {
        this.diskEnabled = true;
        return this;
    }

    /**
     * Change the default disk location and enable diskCaching
     */
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
     * Update the values when adding key/value pairs alread in the cache
     * @param updateExisting 
     */
    public CacheBuilder updateExisting(boolean updateExisting) {
        this.updateExisting = updateExisting;
        return this;
    }

	/**
	 * Change the default pattern for creating subfolders 
	 * Must be time based and | will be replaced with File.separator ex: "yyyyMMdd|hh|mm|"
	 */
    public CacheBuilder subfolderPattern(String pattern) {
        this.subFoldersPattern = pattern;
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

	/**
	 * @return the subFolderPatterns
	 */
	public String getSubFoldersPattern() {
		return subFoldersPattern;
	}

	/**
	 * @param subFolderPatterns the subFolderPatterns to set
	 */
	public void setSubFolderPatterns(String subFolderPatterns) {
		this.subFoldersPattern = subFolderPatterns;
	}

    
}