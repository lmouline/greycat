package org.mwg.struct;

public interface ENode {

    ENode set(String name, byte type, Object value);

    ENode setAt(long key, byte type, Object value);

    ENode add(String name);

    ENode addAt(long key);

    Object get(String name);

    Object getAt(long key);

    long id();

    void drop();

}
