package com.sbiz.cache.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

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

	private String diskLocation;

	public DiskStore(String diskLocation) {
		this.diskLocation = diskLocation + "/jcache/";
		this.size = 0;
	}

	public void remove(K key) {
		File fileToRemove = new File(getFileName(key));
		if (fileToRemove.delete())
			size--;
	}

	public void add(K key, V value) {
		try {
			File file = new File(getFileName(key));
			FileOutputStream fos = new FileOutputStream(file);
			ObjectOutputStream out = new ObjectOutputStream(fos);
			FileEntry<V> obj = new FileEntry<V>(value);
			out.writeObject(obj);
			out.close();
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
			FileEntry<V> obj = (FileEntry<V>)in.readObject();
			in.close();
			return ((FileEntry<V>)obj).value;
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
					.append(String.format("%032d", key.hashCode()))
					.toString() ;
	}

}