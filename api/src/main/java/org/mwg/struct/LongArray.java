package org.mwg.struct;

public interface LongArray {

    int size();

    long get(int index);

    void add(long newValue);

    void remove(long oldValue);

    void clear();

}
