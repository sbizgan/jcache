package com.sbiz.cache.implementations;

import java.io.Serializable;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;

import com.sbiz.cache.CacheBuilder;
import com.sbiz.cache.utils.CacheEntry;

/**
 * Least-recently used (LRU) implementation of a cache
 * Sources of inspiration: 
 *  - https://stackoverflow.com/a/23772103/1531903
 *  - https://commons.apache.org/proper/commons-collections/apidocs/src-html/org/apache/commons/collections4/map/LRUMap.html
 */
public class LRUCache<K, V extends Serializable> extends ACache<K, V> {

    private class Node<Key, Value extends Serializable> {
        Node<Key, Value> previous;
        Node<Key, Value> next;
        CacheEntry<Key, Value> cacheEntry;

        public Node(Node<Key, Value> previous, Node<Key, Value> next, CacheEntry<Key, Value> cacheEntry) {
            this.cacheEntry = cacheEntry;
            this.previous = previous;
            this.next = next;
        }

        public Key getKey() {
            return cacheEntry.getKey();
        }
    }

    private ConcurrentHashMap<K, Node<K, V>> cache;

    private Node<K, V> leastRecently;
    private Node<K, V> mostRecently;
    private Node<K, V> leastRecentlyMemory;
    
    public LRUCache() {
        super();
    }

    public LRUCache(CacheBuilder builder) {
        super(builder);
    }

    protected void initializeStrategy() {
        setCacheStrategy(LRU);
        leastRecently = new Node<K, V>(null, null, null);
        mostRecently = leastRecently;
        leastRecentlyMemory = leastRecently;
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

        // Based on LRU strategy new items should be most recent and stored in memory
        // Move the least recent item from memory to disk
        // Always update the stores before removing items
        demoteLeastRecentMemory();

        // Now make sure we have space for the new value

        // Delete the left-most entry and update the LRU pointer
        if (size == getMaxSize()) {
            // Remove from cache
            cache.remove(leastRecently.getKey());

            // Remove value from store
            leastRecently.cacheEntry.removeFromStore();
            leastRecently = leastRecently.next;
            leastRecently.previous = null;

            size--;
        }
        
        // Create a new cache Entry. This will add the value to either memory or disk depending on space availabilty
        CacheEntry<K, V> newEntry = new CacheEntry<K, V>(key, value, store);

        // Put the new node at the right-most end of the linked-list
        Node<K, V> myNode = new Node<K, V>(mostRecently, null, newEntry);
        mostRecently.next = myNode;
        cache.put(key, myNode);
        mostRecently = myNode;

        // Update cache size and for the first added entry update the LRU pointer
        if (size == 0) {
            leastRecently = myNode;
            leastRecentlyMemory = myNode;
            leastRecentlyMemory.next = myNode;
            leastRecently.next = myNode;
        }
        size++;

        if (isPrintInternalsDebug())
            logger.debug("  Strategy info: {}", internals());

    }

    public synchronized V get(K key) {

        logger.debug("{} | Getting object with key {} ", this, key);

        Node<K, V> cachedNode = cache.get(key);
        if (cachedNode == null) {
            return null;
        }

        // If MRU leave the list as it is
        if (cachedNode.getKey().equals(mostRecently.getKey())) {
            return mostRecently.cacheEntry.getValue();
        }

        // Get the next and previous nodes
        Node<K, V> nextNode = cachedNode.next;
        Node<K, V> previousNode = cachedNode.previous;

       
        if (cachedNode.getKey().equals(leastRecently.getKey())) {
            // If at the left-most, we update LR 
            nextNode.previous = null;
            if (leastRecently.equals(leastRecentlyMemory)) 
                //leastRecently still in memory
                leastRecentlyMemory = nextNode;
            else 
                //update and move to disk leastRecentMemory
                demoteLeastRecentMemory();
            leastRecently = nextNode;
            
        } else if (!cachedNode.getKey().equals(mostRecently.getKey())) {
            // If we are in the middle, we need to update the items before and after our item            
            previousNode.next = nextNode;
            nextNode.previous = previousNode;

            if (store.isDiskEnabled()) {
                
                if (cachedNode.cacheEntry.isDiskStored())
                    // Current node on disk memory?
                    demoteLeastRecentMemory();
                else if (cachedNode.getKey().equals(leastRecentlyMemory.getKey())) {
                    // If the leastRecentlyMemory point to the next
                    leastRecentlyMemory = leastRecentlyMemory.next;
                }

            }
        }

        // Finally move our item to the MR
        cachedNode.previous = mostRecently;
        mostRecently.next = cachedNode;
        mostRecently = cachedNode;
        mostRecently.next = null;

        //if mostRecently was on disk -> switch to memory
        if (mostRecently.cacheEntry.isDiskStored())
            mostRecently.cacheEntry.switchStore();

        if (isPrintInternalsDebug())
            logger.debug("  Strategy info: {}", internals());

        return cachedNode.cacheEntry.getValue();

    }

    // Move least recent object to disk
    private synchronized void demoteLeastRecentMemory() {
        if (store.isDiskEnabled() && store.isMemoryFull() ) {
            boolean movedToDisk = leastRecentlyMemory.cacheEntry.switchStore();
            logger.debug("  {} moved to {}", leastRecentlyMemory.getKey(), (movedToDisk ? "disk" : "memory"));
            leastRecentlyMemory = leastRecentlyMemory.next;
        }
    }

    // Move least recent object to memory
    private synchronized void promoteLeastRecentMemory() {
        if (store.isDiskEnabled()) {
            Node<K, V> prevNode = leastRecentlyMemory.previous;
            // see if leastRecentlyMemory is last and if previous is diskStored
            if (prevNode != null && prevNode.cacheEntry.isDiskStored()) {
                boolean moveToMemory = prevNode.cacheEntry.switchStore();
                logger.debug("  {} moved to {}", prevNode.getKey(), (moveToMemory ? "disk" : "memory"));
                leastRecentlyMemory = prevNode;
            }
        }
    }

    public boolean containsKey(K key) {
        return cache.containsKey(key);
    }

    public synchronized V remove(K key) {
        logger.debug("{} | Removing object with key {} ", this, key);

        Node<K, V> currentNode = cache.get(key);
        if (currentNode == null) {
            return null;
        }

        // Get the next and previous nodes
        Node<K, V> nextNode = currentNode.next;
        Node<K, V> previousNode = currentNode.previous;

        // Remove the object from the cache
        cache.remove(key);

        // If current node is in memory then promote one from disk (if possible) 
        if (!currentNode.cacheEntry.isDiskStored())
            promoteLeastRecentMemory(); 

        if (currentNode.getKey().equals(mostRecently.getKey())) {
            // MR
            previousNode.next = null;
            currentNode.previous = null;
            mostRecently = previousNode;
        } else if (currentNode.getKey().equals(leastRecently.getKey())) {
            // LR
            nextNode.previous = null;
            currentNode.next = null;
            leastRecently = nextNode;
        } else {
            // Middle
            previousNode.next = nextNode;
            nextNode.previous = previousNode;
            currentNode.next = null;
            currentNode.previous = null;
        }
        size--;
        V removedValue = currentNode.cacheEntry.removeFromStore();
        
        if (isPrintInternalsDebug())
            logger.debug("  Strategy info: {}", internals());
        
        return removedValue;
    }

    public boolean isEmpty() {
        return cache.isEmpty();
    }

    public synchronized void clear() {
        //clear map
        cache.clear();
        store.clear();

        //reinitilize internals 
        leastRecently = new Node<K, V>(null, null, null);
        mostRecently = leastRecently;
        leastRecentlyMemory = leastRecently;
        size = 0;
    }

    @Override
    public String internals() {
        StringBuffer sb = new StringBuffer("  Old | ");
        Node<K, V> current = leastRecently;
        sb.append(current.getKey())
            .append("[")
            .append(current.cacheEntry.isDiskStored() ? "D": "M")
            .append("]");
        while (!current.equals(mostRecently)) {
            current = current.next;
            sb.append("-").append(current.getKey()) 
                .append("[")
                .append(current.cacheEntry.isDiskStored()? "D": "M")
                .append("]");
        }
        sb.append(" | New     |   ").append(store.toString());
        return sb.toString();
    }

    // Method used for unit testing purposes!
    public boolean isEntryDiskStored(K key) {
        Node<K, V> foundNode = cache.get(key);
        if (foundNode == null)
            throw new NoSuchElementException(key + " not in cache");
        else  
            return foundNode.cacheEntry.isDiskStored();
    }

}