package com.sbiz.cache;

import java.io.File;

public interface CacheDefaults {
    
    /**
     * Default max number of objects to store in memory
     */
    public static final int DEFAULT_MAX_SIZE_MEMORY = 100;

    /**
     * Default max number of objects to store on disk
     */
    public static final int DEFAULT_MAX_SIZE_DISK = 1000;
    
    /**
     * Set user home as default cache location
     */
    public static final String DEFAULT_DISK_LOCATION = System.getProperty("user.home") 
                                    + File.separator + ".jcache" + File.separator;

    /**
     * By default disk cache is disabled
     */ 
    public static final boolean DEFAULT_DISK_ENABLED = false;

    /**
     * If for a key there is already a value stored in the cache 
     * by default we will update the existing value
     */
    public static final boolean DEFAULT_UPDATE_EXISTING = true;
    
    /**
     * For debug purposes. Set as true for printing details on storead information
     */
    public static final boolean DEFAULT_PRINT_INTERNALS_DEBUG = false;

    public static final String LRU = "LRU";

    public static final String LFU = "LFU";

}