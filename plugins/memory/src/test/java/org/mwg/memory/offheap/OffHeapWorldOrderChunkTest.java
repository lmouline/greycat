package org.mwg.memory.offheap;

import org.mwg.core.chunk.AbstractWorldOrderChunkTest;

public class OffHeapWorldOrderChunkTest extends AbstractWorldOrderChunkTest {

    public OffHeapWorldOrderChunkTest() {
        super(new OffHeapMemoryFactory());
    }

}
