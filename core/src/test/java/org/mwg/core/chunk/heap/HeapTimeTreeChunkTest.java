package org.mwg.core.chunk.heap;

import org.mwg.core.chunk.AbstractTimeTreeTest;
import org.mwg.core.memory.HeapMemoryFactory;

public class HeapTimeTreeChunkTest extends AbstractTimeTreeTest {

    public HeapTimeTreeChunkTest() {
        super(new HeapMemoryFactory());
    }

}
