package org.mwg.struct;

public interface Relationship {

    int size();

    long get(int index);

    void set(int index, long value);

    Relationship add(long newValue);

    Relationship remove(long oldValue);

    Relationship clear();

}
