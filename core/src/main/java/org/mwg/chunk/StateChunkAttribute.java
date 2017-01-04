package org.mwg.chunk;

import org.mwg.struct.Buffer;

public interface StateChunkAttribute {

    int load(Buffer target);

    void save(Buffer target);

    StateChunkAttribute clone();

}
