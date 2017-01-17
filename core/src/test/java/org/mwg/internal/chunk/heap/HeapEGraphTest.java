package org.mwg.internal.chunk.heap;

import org.mwg.internal.chunk.AbstractEGraphTest;
import org.mwg.internal.memory.HeapMemoryFactory;

public class HeapEGraphTest extends AbstractEGraphTest {

    public HeapEGraphTest() {
        super(new HeapMemoryFactory());
    }
}
