package org.mwg.chunk;

public interface ChunkIterator {

    boolean hasNext();

    Chunk next();

    long size();

    void free();

}
