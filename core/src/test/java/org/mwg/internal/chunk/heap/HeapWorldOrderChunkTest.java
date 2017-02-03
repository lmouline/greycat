package org.mwg.internal.chunk.heap;

import org.mwg.internal.chunk.AbstractWorldOrderChunkTest;
import org.mwg.internal.memory.HeapMemoryFactory;

public class HeapWorldOrderChunkTest extends AbstractWorldOrderChunkTest {

    public HeapWorldOrderChunkTest() {
        super(new HeapMemoryFactory());
    }

}
