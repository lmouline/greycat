package org.mwg.memory.offheap;

import org.mwg.core.chunk.AbstractEGraphTest;
import org.mwg.core.memory.HeapMemoryFactory;

public class OffHeapEGraphTest extends AbstractEGraphTest {

    public OffHeapEGraphTest() {
        super(new OffHeapMemoryFactory());
    }
}
