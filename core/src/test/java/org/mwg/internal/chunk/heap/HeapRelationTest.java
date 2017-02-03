package org.mwg.internal.chunk.heap;

import org.mwg.internal.chunk.AbstractRelationTest;
import org.mwg.internal.memory.HeapMemoryFactory;

public class HeapRelationTest extends AbstractRelationTest {

    public HeapRelationTest() {
        super(new HeapMemoryFactory());
    }

}
