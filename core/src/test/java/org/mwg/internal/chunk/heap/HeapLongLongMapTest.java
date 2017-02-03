package org.mwg.internal.chunk.heap;

import org.mwg.internal.chunk.AbstractLongLongMapTest;
import org.mwg.internal.memory.HeapMemoryFactory;

public class HeapLongLongMapTest extends AbstractLongLongMapTest {

    public HeapLongLongMapTest() {
        super(new HeapMemoryFactory());
    }

}
