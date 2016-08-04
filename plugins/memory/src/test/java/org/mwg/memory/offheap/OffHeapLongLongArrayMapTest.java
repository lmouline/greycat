package org.mwg.memory.offheap;

import org.mwg.core.chunk.AbstractLongLongArrayMapTest;

public class OffHeapLongLongArrayMapTest extends AbstractLongLongArrayMapTest {

    public OffHeapLongLongArrayMapTest() {
        super(new OffHeapMemoryFactory());
    }

}
