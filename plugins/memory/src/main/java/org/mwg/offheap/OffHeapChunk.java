package org.mwg.offheap;

import org.mwg.chunk.Chunk;

/**
 * @ignore ts
 */
public interface OffHeapChunk extends Chunk {

    long addr();

    void setIndex(long index);

}
