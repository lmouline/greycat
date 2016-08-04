package org.mwg.memory.offheap;

import org.mwg.core.chunk.AbstractStateChunkTest;

public class OffHeapStateChunkTest extends AbstractStateChunkTest {

    public OffHeapStateChunkTest() {
        super(new OffHeapMemoryFactory());
    }


}
