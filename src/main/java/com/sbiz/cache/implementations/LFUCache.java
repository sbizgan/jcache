package com.sbiz.cache.implementations;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.sbiz.cache.CacheBuilder;
import com.sbiz.cache.utils.CacheEntry;

/**
 * Least-frequently used (LFU) implementation of a cache
 * Sources if inspiration:
 * 	- http://www.javarticles.com/2012/06/lfu-cache.html
 */
public class LFUCache<K, V extends Serializable> extends ACache<K, V> {

	private class Node<Key, Value extends Serializable> {
		public CacheEntry<Key, Value> cacheEntry;
		public int frequency;

		public Node(CacheEntry<Key, Value> cacheEntry, int frequency) {
			this.cacheEntry = cacheEntry; 
			this.frequency = frequency;
		}

		public Key getKey() {
            return cacheEntry.getKey();
        }
	}

	private ConcurrentHashMap<K, Node<K, V>> cache;
	private ConcurrentHashMap<Integer, HashSet<K>> frequencies;

	public LFUCache() {
		super();
	}

	public LFUCache(CacheBuilder builder) {
		super(builder);
	}

	protected void initializeStrategy() {
		setCacheStrategy(LFU);
		frequencies = new ConcurrentHashMap<Integer, HashSet<K>>();
		cache = new ConcurrentHashMap<K, Node<K, V>>();
		logger.debug("{} | {} Cache initialized", this, cacheStrategy);
	}

	public void put(K key, V value) {
		if (cache.containsKey(key)) {
			if (isUpdateExisting())
					cache.get(key).cacheEntry.updateValue(value);
			return;
		}

		// new object -> add to the least frequent list
		HashSet<K> leastFrequent = frequencies.get(0);
		if (leastFrequent == null)
			leastFrequent = new HashSet<K>();

		if (size == getMaxSize()) {
			doEviction();
		}

		leastFrequent.add(key);

		frequencies.put(0, leastFrequent);
		
		CacheEntry<K, V> newEntry = new CacheEntry<K, V>(key, value, store);

		Node<K, V> newNode = new Node<K, V>(newEntry, 0);
		cache.put(key, newNode);
		size++;

		super.put(key, value);
	}

	// O(N) complexity comes from this method
	// Worst case scenario all cached items are all the same freqeuncy 
	// so we have to go through all frequencies
	private void doEviction() {
		// Depeding on the first item found this should be first from disk if memory is full
		// TODO verify

		// make some room for new node
		boolean evicted = false;
		int index = 0;
		while (!evicted) {
			HashSet<K> frequency = frequencies.get(index);
			if (!frequency.isEmpty()) {
				K key = frequency.iterator().next();
				frequency.remove(key);
				store.remove(cache.get(key).cacheEntry);
				cache.remove(key);
				evicted = true;
			}
			index++;
		}
		size--;
	}

	public V get(K key) {
		Node<K, V> cachedNode = cache.get(key);
		if (cachedNode == null) {
			return null;
		}

		// remove from current frequency
		frequencies.get(cachedNode.frequency).remove(key);

		// increase node freqeuncy
		cachedNode.frequency++;

		// add the new freqeuncy list if it does not exist
		if (!frequencies.containsKey(cachedNode.frequency))
			frequencies.put(cachedNode.frequency, new HashSet<K>());

		// add the current key to the frequency list
		frequencies.get(cachedNode.frequency).add(key);

		return cachedNode.cacheEntry.getValue();
	}

	public boolean containsKey(K key) {
		return cache.contains(key);
	}

	public V remove(K key) {
		Node<K, V> cachedNode = cache.get(key);
		if (cachedNode == null) {
			return null;
		}

		frequencies.get(cachedNode.frequency).remove(key);
		store.remove(cache.get(key).cacheEntry);
		cache.remove(key);
		return cachedNode.cacheEntry.getValue();
	}

	public boolean isEmpty() {
		return cache.isEmpty();
	}

	public int size() {
		return size;
	}

	public void clear() {
		cache.clear();
		frequencies.clear();
		store.clear();
		size = 0;
	}

	@Override
	public String internals() {
		StringBuilder sb = new StringBuilder();
		for (Entry<K, Node<K, V>> entry : cache.entrySet()) {
			Node<K, V> node = entry.getValue();
			sb.append(node.getKey()).append("(").append(node.frequency).append(") ");
		}
		sb.append(" | ");
		for (Entry<Integer, HashSet<K>> entry : frequencies.entrySet()) {
			sb.append(entry.getKey()).append(":");
			for (K key : entry.getValue()) {
				sb.append(key).append(" ");
			}
		}

		return sb.toString();
	}

}