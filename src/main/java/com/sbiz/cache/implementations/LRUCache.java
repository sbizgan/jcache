package com.sbiz.cache.implementations;

import java.util.concurrent.ConcurrentHashMap;

import com.sbiz.cache.CacheBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Least-recently used (LRU) implementation of a cache
 * Sources of inspiration: 
 *  - https://stackoverflow.com/a/23772103/1531903
 *  - https://commons.apache.org/proper/commons-collections/apidocs/src-html/org/apache/commons/collections4/map/LRUMap.html
 */
public class LRUCache<K, V> extends ACache<K, V> {

    private class Node<Key, Value> {
        Node<Key, Value> previous;
        Node<Key, Value> next;
        Key key;
        Value value;

        public Node(Node<Key, Value> previous, Node<Key, Value> next, Key key, Value value) {
            this.previous = previous;
            this.next = next;
            this.key = key;
            this.value = value;
        }
    }

    private ConcurrentHashMap<K, Node<K, V>> cache;
    private Node<K, V> leastRecentlyUsed;
    private Node<K, V> mostRecentlyUsed;

    public LRUCache() {
        super();
    }

    public LRUCache(CacheBuilder builder) {
        super(builder);
    }
    
    protected void initialize() {
        setCacheStrategy("LRU");
        leastRecentlyUsed = new Node<K, V>(null, null, null, null);
        mostRecentlyUsed = leastRecentlyUsed;
        cache = new ConcurrentHashMap<K, Node<K, V>>();
        logger.debug("{} | Cache initialized", this);
    }

    public void put(K key, V value) {
        if (cache.containsKey(key)) {
            if (isUpdateExisting())
                cache.get(key).value = value;
            return;
        }

        // Put the new node at the right-most end of the linked-list
        Node<K, V> myNode = new Node<K, V>(mostRecentlyUsed, null, key, value);
        mostRecentlyUsed.next = myNode;
        cache.put(key, myNode);
        mostRecentlyUsed = myNode;

        // Delete the left-most entry and update the LRU pointer
        if (size == getMaxSize()) {
            cache.remove(leastRecentlyUsed.key);
            leastRecentlyUsed = leastRecentlyUsed.next;
            leastRecentlyUsed.previous = null;
        }

        // Update cache size, for the first added entry update the LRU pointer
        else if (size < getMaxSize()) {
            if (size == 0) {
                leastRecentlyUsed = myNode;
            }
            size++;
        }

        super.put(key, value);
    }

    public V get(K key) {
        Node<K, V> cachedNode = cache.get(key);
        if (cachedNode == null) {
            return null;
        }
        // If MRU leave the list as it is
        else if (cachedNode.key == mostRecentlyUsed.key) {
            return mostRecentlyUsed.value;
        }

        // Get the next and previous nodes
        Node<K, V> nextNode = cachedNode.next;
        Node<K, V> previousNode = cachedNode.previous;

        // If at the left-most, we update LRU 
        if (cachedNode.key == leastRecentlyUsed.key) {
            nextNode.previous = null;
            leastRecentlyUsed = nextNode;
        }

        // If we are in the middle, we need to update the items before and after our item
        else if (cachedNode.key != mostRecentlyUsed.key) {
            previousNode.next = nextNode;
            nextNode.previous = previousNode;
        }

        // Finally move our item to the MRU
        cachedNode.previous = mostRecentlyUsed;
        mostRecentlyUsed.next = cachedNode;
        mostRecentlyUsed = cachedNode;
        mostRecentlyUsed.next = null;

        return cachedNode.value;
    }

    public boolean containsKey(K key) {
        return cache.containsKey(key);
    }

    public V remove(K key) {

        Node<K, V> currentNode = cache.get(key);
        if (currentNode == null) {
            return null;
        }

        // Get the next and previous nodes
        Node<K, V> nextNode = currentNode.next;
        Node<K, V> previousNode = currentNode.previous;

        // Remove the object from the cache
        cache.remove(key);

        // Update internal linked list

        // If MRU
        if (currentNode.key == mostRecentlyUsed.key) {
            previousNode.next = null;
            currentNode.previous = null;
            mostRecentlyUsed = previousNode;
            size--;
            return currentNode.value;
        }

        // If LRU
        if (currentNode.key == leastRecentlyUsed.key) {
            nextNode.previous = null;
            currentNode.next = null;
            leastRecentlyUsed = nextNode;
            size--;
            return currentNode.value;
        }

        // If middle
        previousNode.next = nextNode;
        nextNode.previous = previousNode;
        currentNode.next = null;
        currentNode.previous = null;
        size--;
        return currentNode.value;
    }

    public boolean isEmpty() {
        return cache.isEmpty();
    }

    public void clear() {
        //clear map
        cache.clear();

        //reinitilize internals 
        leastRecentlyUsed = new Node<K, V>(null, null, null, null);
        mostRecentlyUsed = leastRecentlyUsed;
        size = 0;
    }

	@Override
	public String internals() {
        StringBuffer sb = new StringBuffer();
        Node<K, V> current = leastRecentlyUsed;
        sb.append(current.key);
        while(!current.equals(mostRecentlyUsed)) {
            sb.append(">").append(current.next.key);
            current = current.next;
        }
        return sb.toString();
	}

}