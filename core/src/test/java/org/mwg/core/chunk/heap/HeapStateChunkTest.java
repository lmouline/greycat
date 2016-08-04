package org.mwg.core.chunk.heap;

import org.mwg.core.chunk.AbstractStateChunkTest;
import org.mwg.core.memory.HeapMemoryFactory;

public class HeapStateChunkTest extends AbstractStateChunkTest {

    public HeapStateChunkTest() {
        super(new HeapMemoryFactory());
    }


}
