package com.sbiz.cache.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;

import com.sbiz.cache.CacheDefaults;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Refrences:
 * - https://www.cacheonix.org/articles/How_to_Cache_a_File_in_Java.htm
 * 	
 * 		TODO lock this diskLocation (so no other cache can use it)
 * 		DONE add subdirectories based on yyyyMMdd\hh\mm (keep locations on CacheEntry)
 * 		TODO manage empty subfolders when deleting!
 * 		TODO manage situations where files / directories are deleted externally
 */

public class DiskStore<K, V extends Serializable> {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

	private static String subfolderPattern = CacheDefaults.DEFAULT_SUBFOLDERS_PATTERN;

	private int size;

	private long diskSize;

    // Disk location of the cache
    private String diskLocation;

	public DiskStore() {
		this.size = 0;
		this.diskSize = 0;
		setDiskLocation(CacheDefaults.DEFAULT_DISK_LOCATION);
	}

	/**
	 * Create subfolders for cache entires based on current time
	 */
	public static String getNextSubFolder() {
		return 
			new SimpleDateFormat(subfolderPattern)
					.format(System.currentTimeMillis())
					.replace("|", File.separator);
	}

	public void remove(CacheEntry<K, V> cacheEntry) {
		File fileToRemove = new File(getFileName(cacheEntry));
		long fileSize = fileToRemove.length();
		if (fileToRemove.delete()) {
			size--;
			diskSize -= fileSize;
		}
	}

	public void addUpdate(CacheEntry<K, V> cacheEntry, V value, boolean add) {
		try {
			// Create folders
			new File(getEntryFolder(cacheEntry)).mkdirs();
			File file = new File(getFileName(cacheEntry));
			
			FileOutputStream fos = new FileOutputStream(file);
			ObjectOutputStream out = new ObjectOutputStream(fos);
			out.writeObject(value);
			out.close();
			diskSize += file.length();
			if (add)
				size++;
		} catch (Exception e) {
			logger.error("We've got an error writing cache entry to file: {}",
					e.getLocalizedMessage());
		}
	}

	public V getValue(CacheEntry<K, V> cacheEntry) {
		try {
			FileInputStream file = new FileInputStream(new File(getFileName(cacheEntry)));
			ObjectInputStream in = new ObjectInputStream(file);
			@SuppressWarnings("unchecked") 
				V value = (V)in.readObject();
			in.close();
			return value;
		} catch (Exception e) {
			logger.error("We've got an error writing cache entry to file: {}", e.getLocalizedMessage());
		}
		return null;
	}

	public int size() {
		return size;
	}

	private String getEntryFolder(CacheEntry<K, V> cacheEntry) {
		return new StringBuilder(diskLocation)
					.append(cacheEntry.getSubFolder())
					.toString() ;
	}
	
	private String getFileName(CacheEntry<K, V> cacheEntry) {
		return new StringBuilder(diskLocation)
					.append(cacheEntry.getSubFolder())
					.append(cacheEntry.getKey().hashCode())
					.toString() ;
	}

	public void setDiskLocation(String diskLocation) {
		if (diskLocation.lastIndexOf(File.separator) != (diskLocation.length()-1)) 
			diskLocation += File.separator;
		this.diskLocation = diskLocation;
	}

	/**
	 * Change the default pattern for creating subfolders 
	 * Must be one that is based on time and | separator ex: "yyyyMMdd|hh|mm|"
	 */
	public void setSubFoldersPattern(String pattern) {
		subfolderPattern = pattern;
	}

	public void initLocation() {
		new File(diskLocation).mkdirs();
	}

	public long getDiskSize() {
		return diskSize;
	}

	public void clear() {
		delete(new File(diskLocation));
	}

	private void delete(File f) {
		if (f.isDirectory()) {
		  for (File c : f.listFiles())
			delete(c);
		}
		if (!f.delete())
		  throw new SecurityException("Failed to delete file: " + f);
	}

}