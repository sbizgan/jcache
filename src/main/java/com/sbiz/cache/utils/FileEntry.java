package com.sbiz.cache.utils;

import java.io.Serializable;

public class FileEntry<V extends Serializable> implements Serializable {

	private static final long serialVersionUID = 123L;

    V value;

    FileEntry(V value) {
        this.value = value;
    }

    public V getValue() {
        return value;
    }

    private void setValue(V value) {
        this.value = value;
    }

}