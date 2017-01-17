package org.mwg.internal.chunk.heap;

import org.mwg.internal.chunk.AbstractStringLongMapTest;
import org.mwg.internal.memory.HeapMemoryFactory;

public class HeapStringLongMapTest extends AbstractStringLongMapTest {

    public HeapStringLongMapTest() {
        super(new HeapMemoryFactory());
    }

}
