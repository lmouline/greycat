package org.mwg.memory.offheap;

import org.mwg.internal.chunk.AbstractEGraphTest;
import org.mwg.internal.memory.HeapMemoryFactory;

public class OffHeapEGraphTest extends AbstractEGraphTest {

    public OffHeapEGraphTest() {
        super(new OffHeapMemoryFactory());
    }
}
