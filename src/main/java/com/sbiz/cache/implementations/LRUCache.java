package com.sbiz.cache.implementations;

import java.util.HashMap;

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

    Logger logger = LoggerFactory.getLogger(LRUCache.class);

    class Node<T, U> {
        Node<T, U> previous;
        Node<T, U> next;
        T key;
        U value;

        public Node(Node<T, U> previous, Node<T, U> next, T key, U value) {
            this.previous = previous;
            this.next = next;
            this.key = key;
            this.value = value;
        }
    }

    private HashMap<K, Node<K, V>> cache;
    private Node<K, V> leastRecentlyUsed;
    private Node<K, V> mostRecentlyUsed;

    private int size;

    public LRUCache() {
        leastRecentlyUsed = new Node<K, V>(null, null, null, null);
        mostRecentlyUsed = leastRecentlyUsed;
        cache = new HashMap<K, Node<K, V>>();
        size = 0;

        logger.debug("Logger created with id {}", this);
    }

    public LRUCache(CacheBuilder builder) {
        this();

        setDiskLocation(builder.getDiskLocation());
        setDiskEnabled(builder.isDiskEnabled());
        setMaxSize(builder.getMaxSize());

        logger.debug("... defaults modified");
    }

    public void put(K key, V value) {
        if (cache.containsKey(key)) {
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

        logger.debug("Object added. Size: {}", size);
    }

    public V get(K key) {
        Node<K, V> currentNode = cache.get(key);
        if (currentNode == null) {
            return null;
        }
        // If MRU leave the list as it is
        else if (currentNode.key == mostRecentlyUsed.key) {
            return mostRecentlyUsed.value;
        }

        // Get the next and previous nodes
        Node<K, V> nextNode = currentNode.next;
        Node<K, V> previousNode = currentNode.previous;

        // If at the left-most, we update LRU 
        if (currentNode.key == leastRecentlyUsed.key) {
            nextNode.previous = null;
            leastRecentlyUsed = nextNode;
        }

        // If we are in the middle, we need to update the items before and after our item
        else if (currentNode.key != mostRecentlyUsed.key) {
            previousNode.next = nextNode;
            nextNode.previous = previousNode;
        }

        // Finally move our item to the MRU
        currentNode.previous = mostRecentlyUsed;
        mostRecentlyUsed.next = currentNode;
        mostRecentlyUsed = currentNode;
        mostRecentlyUsed.next = null;

        return currentNode.value;
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

        // Update internals

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

    public int size() {
        return size;
    }

    public void clear() {
        //clear map
        cache.clear();

        //reinitilize internals 
        leastRecentlyUsed = new Node<K, V>(null, null, null, null);
        mostRecentlyUsed = leastRecentlyUsed;
        size = 0;
    }

}