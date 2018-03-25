package com.sbiz.cache.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Refrences:
 * - https://www.cacheonix.org/articles/How_to_Cache_a_File_in_Java.htm
 */

public class DiskStore<K, V> {

	class FileEntry implements Serializable {
		
		private static final long serialVersionUID = 14412341235346L;
		
		V value;

		FileEntry(V value) {
			this.value = value;
		}
		
	}

	private String diskLocation;

	public DiskStore(String diskLocation) {
		this.diskLocation = diskLocation + "/jcache/";
		// lock this diskLocation (so no other cache can use it)
	}

	public void remove(K key) {
	}

	public void add(K key, V value) {
		try {
			FileOutputStream file = new FileOutputStream(new File(diskLocation + key.hashCode()));
			ObjectOutputStream out = new ObjectOutputStream(file);
			Object obj = new FileEntry(value);
			out.writeObject(obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public V getValue(K key) {
		return null;
	}

	public int size() {
		return 0;
	}

}