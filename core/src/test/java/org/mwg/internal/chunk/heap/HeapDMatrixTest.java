package org.mwg.internal.chunk.heap;

import org.mwg.internal.chunk.AbstractMatrixTest;
import org.mwg.internal.memory.HeapMemoryFactory;

public class HeapDMatrixTest extends AbstractMatrixTest {

    public HeapDMatrixTest() {
        super(new HeapMemoryFactory());
    }

}
