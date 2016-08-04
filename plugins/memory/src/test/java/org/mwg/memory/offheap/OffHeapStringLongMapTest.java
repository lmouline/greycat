package org.mwg.memory.offheap;

import org.mwg.core.chunk.AbstractStringLongMapTest;

public class OffHeapStringLongMapTest extends AbstractStringLongMapTest {

    public OffHeapStringLongMapTest() {
        super(new OffHeapMemoryFactory());
    }

}
