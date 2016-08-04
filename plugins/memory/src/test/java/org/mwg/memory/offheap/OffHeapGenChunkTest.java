package org.mwg.memory.offheap;

import org.mwg.core.chunk.AbstractGenChunkTest;

public class OffHeapGenChunkTest extends AbstractGenChunkTest {

    public OffHeapGenChunkTest() {
        super(new OffHeapMemoryFactory());
    }

}
