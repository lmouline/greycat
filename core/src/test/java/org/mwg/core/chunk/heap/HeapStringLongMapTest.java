package org.mwg.core.chunk.heap;

import org.mwg.core.chunk.AbstractStringLongMapTest;
import org.mwg.core.memory.HeapMemoryFactory;

public class HeapStringLongMapTest extends AbstractStringLongMapTest {

    public HeapStringLongMapTest() {
        super(new HeapMemoryFactory());
    }

}
