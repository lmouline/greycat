package org.mwg.memory.offheap;

import org.mwg.core.chunk.AbstractLongLongMapTest;

public class OffHeapLongLongMapTest extends AbstractLongLongMapTest {

    public OffHeapLongLongMapTest() {
        super(new OffHeapMemoryFactory());
    }

}
