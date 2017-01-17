package org.mwg.internal.chunk.heap;

import org.mwg.internal.chunk.AbstractTimeTreeTest;
import org.mwg.internal.memory.HeapMemoryFactory;

public class HeapTimeTreeChunkTest extends AbstractTimeTreeTest {

    public HeapTimeTreeChunkTest() {
        super(new HeapMemoryFactory());
    }

}
