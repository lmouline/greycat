package org.mwg.memory.offheap;

import org.mwg.core.chunk.AbstractTimeTreeTest;

public class OffHeapTimeTreeChunkTest extends AbstractTimeTreeTest {

    public OffHeapTimeTreeChunkTest() {
        super(new OffHeapMemoryFactory());
    }

}
