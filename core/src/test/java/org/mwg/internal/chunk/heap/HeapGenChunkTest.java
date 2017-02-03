package org.mwg.internal.chunk.heap;

import org.mwg.internal.chunk.AbstractGenChunkTest;
import org.mwg.internal.memory.HeapMemoryFactory;

public class HeapGenChunkTest extends AbstractGenChunkTest {

    public HeapGenChunkTest() {
        super(new HeapMemoryFactory());
    }

}
