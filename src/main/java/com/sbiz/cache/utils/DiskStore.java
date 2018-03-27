package com.sbiz.cache.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import com.sbiz.cache.CacheDefaults;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Refrences:
 * - https://www.cacheonix.org/articles/How_to_Cache_a_File_in_Java.htm
 * 	
 * TODO lock this diskLocation (so no other cache can use it)
 * TODO add subdirectories based on yyyyMMdd\hh\mm (keep locations on CacheEntry)
 * 		TODO manage empty subfolders when deleting!
 */

public class DiskStore<K, V extends Serializable> {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

	private int size;

	private long diskSize;

    // Disk location of the cache
    private String diskLocation;

	public DiskStore() {
		this.size = 0;
		this.diskSize = 0;
		setDiskLocation(CacheDefaults.DEFAULT_DISK_LOCATION + File.separator + ".jcache" + File.separator);
	}

	public void remove(K key) {
		File fileToRemove = new File(getFileName(key));
		long fileSize = fileToRemove.length();
		if (fileToRemove.delete()) {
			size--;
			diskSize -= fileSize;
		}
	}

	public void add(K key, V value) {
		try {
			File file = new File(getFileName(key));
			FileOutputStream fos = new FileOutputStream(file);
			ObjectOutputStream out = new ObjectOutputStream(fos);
			out.writeObject(value);
			out.close();
			diskSize += file.length();
			size++;
		} catch (Exception e) {
			logger.error("We've got an error writing cache entry to file: {}",
					e.getLocalizedMessage());
			e.printStackTrace();
		}
	}


	public V getValue(K key) {
		try {
			FileInputStream file = new FileInputStream(new File(getFileName(key)));
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
	
	private String getFileName(K key) {
		return new StringBuilder(diskLocation)
					.append(key.hashCode())
					.toString() ;
	}

	public void setDiskLocation(String diskLocation) {
		if (diskLocation.lastIndexOf(File.separator) != (diskLocation.length()-1)) 
			diskLocation += File.separator;
		this.diskLocation = diskLocation;
	}

	public void initLocation() {
		new File(diskLocation).mkdirs();
	}

	public long getDiskSize() {
		return diskSize;
	}

}