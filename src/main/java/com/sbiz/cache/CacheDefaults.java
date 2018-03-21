package com.sbiz.cache;

public interface CacheDefaults {
    
    public static final int DEFAULT_MAX_SIZE = 100;
    public static final String DEFAULT_DISK_LOCATION = "";
    public static final boolean DEFAULT_DISK_ENABLED = false;

    /**
     * if for a key there is already a value stored in the cache 
     * by default we will update the existing value
     */
    public static final boolean DEFAULT_UPDATE_EXISTING = true;
    
    // set this to true if internal representations of the strategy are needed in debug 
    public static final boolean DEFAULT_PRINT_INTERNALS_DEBUG = false;
}