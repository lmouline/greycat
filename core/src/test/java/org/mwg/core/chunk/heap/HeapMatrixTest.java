package org.mwg.core.chunk.heap;

import org.mwg.core.chunk.AbstractMatrixTest;
import org.mwg.core.memory.HeapMemoryFactory;

public class HeapMatrixTest extends AbstractMatrixTest {

    public HeapMatrixTest() {
        super(new HeapMemoryFactory());
    }

}
