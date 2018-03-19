package com.sbiz.cache;

public interface CacheDefaults {
    
    public static final int DEFAULT_MAX_SIZE = 100;
    public static final String DEFAULT_DISK_LOCATION = "";
    public static final boolean DEFAULT_DISK_ENABLED = false;
    public static final boolean DEFAULT_UPDATE_EXISTING = false;
    
    // set this to true if internal representations of the strategy are needed in debug 
    public static final boolean DEFAULT_PRINT_INTERNALS_DEBUG = false;
}