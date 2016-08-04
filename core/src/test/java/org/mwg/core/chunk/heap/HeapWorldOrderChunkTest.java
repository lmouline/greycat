package org.mwg.core.chunk.heap;

import org.mwg.core.chunk.AbstractWorldOrderChunkTest;
import org.mwg.core.memory.HeapMemoryFactory;

public class HeapWorldOrderChunkTest extends AbstractWorldOrderChunkTest {

    public HeapWorldOrderChunkTest() {
        super(new HeapMemoryFactory());
    }

}
