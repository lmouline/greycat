package org.mwg.core.chunk.heap;

import org.junit.Test;
import org.mwg.core.chunk.AbstractFixedStackTest;

public class HeapFixedStackTest extends AbstractFixedStackTest {

    @Test
    public void heapFixedStackTest() {
        test(new HeapFixedStack(CAPACITY, true));
    }

}
