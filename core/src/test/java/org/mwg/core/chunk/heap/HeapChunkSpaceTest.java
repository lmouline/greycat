package org.mwg.core.chunk.heap;

import org.mwg.core.chunk.AbstractChunkSpaceTest;
import org.mwg.core.memory.HeapMemoryFactory;

public class HeapChunkSpaceTest extends AbstractChunkSpaceTest {

    public HeapChunkSpaceTest() {
        super(new HeapMemoryFactory());
    }

}
