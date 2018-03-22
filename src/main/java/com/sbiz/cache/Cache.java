package com.sbiz.cache;

/**
 * Cache interface <br><br>
 * Use by instantiating an implementation (ex. LRUCache)
 * either using defaults or by providing to the contructor a CacheBuilder
 */
public interface Cache<K, V> {

    void put(K key, V value);

    V get(K key);

    boolean containsKey(K key);

    V remove(K key);

    boolean isEmpty();

    int size();

    void clear();

}