package org.mwg.struct;

public interface ENode {

    long id();

    ENode set(String name, byte type, Object value);

    ENode setAt(long key, byte type, Object value);

    Object get(String name);

    Object getAt(long key);

    Object getOrCreate(final String key, final byte type);

    Object getOrCreateAt(final long key, final byte type);

    void drop();

    EGraph graph();

}
