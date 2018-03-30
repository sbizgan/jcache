package com.sbiz.cache.implementations;

import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
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

	/**
	 * This helps keeping the frequencies
	 */
	private ConcurrentHashMap<Integer, Set<K>> frequencies;

	public LFUCache() {
		super();
	}

	public LFUCache(CacheBuilder builder) {
		super(builder);
	}

	protected void initializeStrategy() {
		setCacheStrategy(LFU);
		frequencies = new ConcurrentHashMap<Integer, Set<K>>();
		cache = new ConcurrentHashMap<K, Node<K, V>>();
		logger.debug("{} | {} Cache initialized", this, cacheStrategy);
	}

	public synchronized void put(K key, V value) {

		logger.debug("{} | Adding object with key {} ", this, key);
		
		if (cache.containsKey(key)) {
			if (isUpdateExisting())
					cache.get(key).cacheEntry.updateValue(value);
			return;
		}

		// new object -> add to the least frequent list
		Set<K> leastFrequents = frequencies.get(0);
		if (leastFrequents == null)
			leastFrequents = Collections.synchronizedSet(new LinkedHashSet<K>());

		if (size == getMaxSize()) {
			evictLeastFrequentItem();
		}

		leastFrequents.add(key);

		frequencies.put(0, leastFrequents);
		
		CacheEntry<K, V> newEntry = new CacheEntry<K, V>(key, value, store);

		Node<K, V> newNode = new Node<K, V>(newEntry, 0);
		cache.put(key, newNode);
		size++;

		super.put(key, value);
	}

	private synchronized void evictLeastFrequentItem() {
		// make some room for new node
		// wost case scenario: all items are on highest frequency 
		//     and we have to go through all freqeuncies and all items in the last frequency
		boolean evicted = false;
		int index = 0;
		while (!evicted) {
			Set<K> frequency = frequencies.get(index);
			if (!frequency.isEmpty()) {
				Iterator<K> keysIterator = frequency.iterator();
				K lastKey = keysIterator.next();

				// we used LinkedHashSet to make sure we delete the last added item on the frequency
				//   this ensures us that if we delete from a frequency set that has memory and disk items
				//   we delete a disk item (which is the last added one)
				while (keysIterator.hasNext()) {
					lastKey = keysIterator.next();
				}

				frequency.remove(lastKey);
				cache.get(lastKey).cacheEntry.removeFromStore();
				cache.remove(lastKey);
				evicted = true;
			}
			index++;
		}
		size--;
	}

	public synchronized V get(K key) {

		logger.debug("{} | Getting object with key {} ", this, key);

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
			frequencies.put(cachedNode.frequency, Collections.synchronizedSet(new LinkedHashSet<K>()));

		// add the current key to the frequency list
		frequencies.get(cachedNode.frequency).add(key);

		// see if other value should be sent to disk
		if (store.isDiskEnabled() && cachedNode.cacheEntry.isDiskStored()) {
			// we have a disk entry - we must see if it overpassed any memory stored value
			// this happens only if first item in previous (not empty frequency) is in memory
			boolean overpassed = false;
			boolean setFound = false;
			boolean stopSearch = false;
			CacheEntry<K, V> lastMemoryEntry = null; //used for finding the last memory entry
			int startingIndex = cachedNode.frequency - 1;

			for (int index = startingIndex; index >=0 && !setFound; index--) {
				Set<K> currentFrequency = frequencies.get(index);
				if (!currentFrequency.isEmpty()) {
					// found the previous not empty frequency
					setFound = true;
					Iterator<K> keysIterator = currentFrequency.iterator();
					
					// look until we've seen all elements or found the key to move to disk
					while (keysIterator.hasNext() && !stopSearch) {
						CacheEntry<K, V> nextEntry = cache.get(keysIterator.next()).cacheEntry;
						if (!nextEntry.isDiskStored()) {
							//we found the first entry that is stored in memory
							overpassed = true;
							//if this is the last memory entry next iteration will not enter this block of code
							lastMemoryEntry = nextEntry; 
						}
						if (overpassed && nextEntry.isDiskStored()) {
							//we've already passed all memory items. we can stop.
							stopSearch = true;
						}
					}


				}
			}

			if (lastMemoryEntry != null) {
				//switch stores
				lastMemoryEntry.switchStore(); //move to disk
				cachedNode.cacheEntry.switchStore(); //move in memory
			}

		}

		return cachedNode.cacheEntry.getValue();
	}

	public boolean containsKey(K key) {
		return cache.contains(key);
	}

	public synchronized V remove(K key) {

		logger.debug("{} | Removing object with key {} ", this, key);

		Node<K, V> cachedNode = cache.get(key);
		if (cachedNode == null) {
			return null;
		}

		CacheEntry<K, V> cachedEntry = cache.get(key).cacheEntry;

		// if this is memory entry move another entry from disk to memory
		if (!cachedEntry.isDiskStored() && store.isDiskEnabled()) {
			
			//find the first previous disk entry
			CacheEntry<K, V> firstPrevEntry = null;
			
			//start from this frequency and item backwards
			int currentFrequency = cachedNode.frequency;
			
			boolean passedCurrent = false;

			while (currentFrequency >= 0 && firstPrevEntry == null) {

				Set<K> currentFrequencySet = frequencies.get(cachedNode.frequency);

				Iterator<K> keysIterator = currentFrequencySet.iterator();
				while (keysIterator.hasNext() && firstPrevEntry == null) {
					CacheEntry<K, V> currentEntry = cache.get(keysIterator.next()).cacheEntry;
					
					if (passedCurrent && currentEntry.isDiskStored()) {
						firstPrevEntry = currentEntry;
					}

					if (currentEntry.getKey().equals(cachedEntry.getKey())) {
						passedCurrent = true;
					}
				}

				currentFrequency--;
			}

			if (firstPrevEntry != null) {
				firstPrevEntry.switchStore();
			}
		}

		frequencies.get(cachedNode.frequency).remove(key);
		V returnValue = store.remove(cachedEntry);
		cache.remove(key);

		return returnValue;
	}

	public boolean isEmpty() {
		return cache.isEmpty();
	}

	public int size() {
		return size;
	}

	public synchronized void clear() {
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
			sb.append(node.getKey())
				.append("(")
					.append(node.frequency).append("|")
					.append(node.cacheEntry.isDiskStored() ? "D" : "M")
				.append(") ");
		}
		sb.append(" | ");
		for (Entry<Integer, Set<K>> entry : frequencies.entrySet()) {
			sb.append(entry.getKey()).append(":");
			for (K key : entry.getValue()) {
				sb.append(key).append(" ");
			}
		}

		return sb.toString();
	}

}