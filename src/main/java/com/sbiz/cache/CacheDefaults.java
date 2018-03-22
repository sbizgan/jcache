package com.sbiz.cache;

public interface CacheDefaults {
    
    /**
     * Default max memory size
     */
    public static final int DEFAULT_MAX_SIZE = 100;

    /**
     * Set user home as default cache location
     */
    public static final String DEFAULT_DISK_LOCATION = System.getProperty("user.home");

    /**
     * By default disk cache is disabled
     */ 
    public static final boolean DEFAULT_DISK_ENABLED = false;

    /**
     * The max size of the disk cache
     */
    public static final int DEFAULT_MEMORY_MAXSIZE = 256;

    /**
     * If for a key there is already a value stored in the cache 
     * by default we will update the existing value
     */
    public static final boolean DEFAULT_UPDATE_EXISTING = true;
    
    /**
     * For debug purposes. Set as true for printing details on storead information
     */
    public static final boolean DEFAULT_PRINT_INTERNALS_DEBUG = false;
    
}