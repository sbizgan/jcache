package com.sbiz.cache.implementations;

import java.util.HashMap;

/**
 * Least-recently used (LRU) implementation of a cache
 * Sources of inspiration: 
 *  - https://stackoverflow.com/a/23772103/1531903
 *  - https://commons.apache.org/proper/commons-collections/apidocs/src-html/org/apache/commons/collections4/map/LRUMap.html
 */
public class LRUCache<K, V> extends ACache<K, V> {

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
    }

    public V get(K key) {
        Node<K, V> tempNode = cache.get(key);
        if (tempNode == null) {
            return null;
        }
        // If MRU leave the list as it is
        else if (tempNode.key == mostRecentlyUsed.key) {
            return mostRecentlyUsed.value;
        }

        // Get the next and previous nodes
        Node<K, V> nextNode = tempNode.next;
        Node<K, V> previousNode = tempNode.previous;

        // If at the left-most, we update LRU 
        if (tempNode.key == leastRecentlyUsed.key) {
            nextNode.previous = null;
            leastRecentlyUsed = nextNode;
        }

        // If we are in the middle, we need to update the items before and after our item
        else if (tempNode.key != mostRecentlyUsed.key) {
            previousNode.next = nextNode;
            nextNode.previous = previousNode;
        }

        // Finally move our item to the MRU
        tempNode.previous = mostRecentlyUsed;
        mostRecentlyUsed.next = tempNode;
        mostRecentlyUsed = tempNode;
        mostRecentlyUsed.next = null;

        return tempNode.value;
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
            return currentNode.value;
        }

        // If LRU
        if (currentNode.key == leastRecentlyUsed.key) {
            nextNode.previous = null;
            currentNode.next = null;
            leastRecentlyUsed = nextNode;
            return currentNode.value;
        }

        // If middle
        previousNode.next = nextNode;
        nextNode.previous = previousNode;
        currentNode.next = null;
        currentNode.previous = null;

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
    }

}