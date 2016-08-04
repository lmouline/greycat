package org.mwg.memory.offheap;

import org.mwg.core.chunk.AbstractChunkSpaceTest;

public class OffHeapChunkSpaceTest extends AbstractChunkSpaceTest {

    public OffHeapChunkSpaceTest() {
        super(new OffHeapMemoryFactory());
    }

}
