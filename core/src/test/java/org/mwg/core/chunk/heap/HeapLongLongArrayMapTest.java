package org.mwg.core.chunk.heap;

import org.mwg.core.chunk.AbstractLongLongArrayMapTest;
import org.mwg.core.memory.HeapMemoryFactory;

public class HeapLongLongArrayMapTest extends AbstractLongLongArrayMapTest {

    public HeapLongLongArrayMapTest() {
        super(new HeapMemoryFactory());
    }

}
