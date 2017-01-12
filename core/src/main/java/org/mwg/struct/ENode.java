package org.mwg.struct;

import org.mwg.plugin.NodeStateCallback;

public interface ENode {

    ENode set(String name, byte type, Object value);

    ENode setAt(int key, byte type, Object value);

    Object get(String name);

    Object getAt(int key);

    Object getOrCreate(final String key, final byte type);

    Object getOrCreateAt(final int key, final byte type);

    void drop();

    EGraph graph();

    void each(final NodeStateCallback callBack);

    ENode clear();

}
