package org.mwg.internal.chunk.heap;

import org.mwg.internal.chunk.AbstractChunkSpaceTest;
import org.mwg.internal.memory.HeapMemoryFactory;

public class HeapChunkSpaceTest extends AbstractChunkSpaceTest {

    public HeapChunkSpaceTest() {
        super(new HeapMemoryFactory());
    }

}
