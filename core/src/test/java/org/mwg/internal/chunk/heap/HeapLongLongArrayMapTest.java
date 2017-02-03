package org.mwg.internal.chunk.heap;

import org.mwg.internal.chunk.AbstractLongLongArrayMapTest;
import org.mwg.internal.memory.HeapMemoryFactory;

public class HeapLongLongArrayMapTest extends AbstractLongLongArrayMapTest {

    public HeapLongLongArrayMapTest() {
        super(new HeapMemoryFactory());
    }

}
