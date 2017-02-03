package org.mwg.internal.chunk.heap;

import org.mwg.internal.chunk.AbstractStringIntMapTest;
import org.mwg.internal.memory.HeapMemoryFactory;

public class HeapStringIntMapTest extends AbstractStringIntMapTest {

    public HeapStringIntMapTest() {
        super(new HeapMemoryFactory());
    }

}
