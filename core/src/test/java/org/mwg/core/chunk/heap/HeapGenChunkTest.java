package org.mwg.core.chunk.heap;

import org.mwg.core.chunk.AbstractGenChunkTest;
import org.mwg.core.memory.HeapMemoryFactory;

public class HeapGenChunkTest extends AbstractGenChunkTest {

    public HeapGenChunkTest() {
        super(new HeapMemoryFactory());
    }

}
