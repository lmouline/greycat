package org.mwg.core.chunk.heap;

import org.mwg.core.chunk.AbstractLongLongMapTest;
import org.mwg.core.memory.HeapMemoryFactory;

public class HeapLongLongMapTest extends AbstractLongLongMapTest {

    public HeapLongLongMapTest() {
        super(new HeapMemoryFactory());
    }

}
