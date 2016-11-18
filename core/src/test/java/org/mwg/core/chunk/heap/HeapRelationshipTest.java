package org.mwg.core.chunk.heap;

import org.mwg.core.chunk.AbstractRelationshipTest;
import org.mwg.core.memory.HeapMemoryFactory;

public class HeapRelationshipTest extends AbstractRelationshipTest {

    public HeapRelationshipTest() {
        super(new HeapMemoryFactory());
    }

}
