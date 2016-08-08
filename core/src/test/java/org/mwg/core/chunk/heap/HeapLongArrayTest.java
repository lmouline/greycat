package org.mwg.core.chunk.heap;

import org.mwg.core.chunk.AbstractLongArrayTest;
import org.mwg.core.chunk.AbstractStringLongMapTest;
import org.mwg.core.memory.HeapMemoryFactory;

public class HeapLongArrayTest extends AbstractLongArrayTest {

    public HeapLongArrayTest() {
        super(new HeapMemoryFactory());
    }

}
