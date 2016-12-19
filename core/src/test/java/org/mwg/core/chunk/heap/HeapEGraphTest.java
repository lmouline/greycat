package org.mwg.core.chunk.heap;

import org.mwg.core.chunk.AbstractEGraphTest;
import org.mwg.core.memory.HeapMemoryFactory;

public class HeapEGraphTest extends AbstractEGraphTest {

    public HeapEGraphTest() {
        super(new HeapMemoryFactory());
    }
}
