package org.mwg.internal.chunk.heap;

import org.mwg.internal.chunk.AbstractStateChunkTest;
import org.mwg.internal.memory.HeapMemoryFactory;

public class HeapStateChunkTest extends AbstractStateChunkTest {

    public HeapStateChunkTest() {
        super(new HeapMemoryFactory());
    }
    
}
