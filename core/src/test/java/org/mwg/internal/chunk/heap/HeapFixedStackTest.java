package org.mwg.internal.chunk.heap;

import org.junit.Test;
import org.mwg.internal.chunk.AbstractFixedStackTest;

public class HeapFixedStackTest extends AbstractFixedStackTest {

    @Test
    public void heapFixedStackTest() {
        test(new HeapFixedStack(CAPACITY, true));
    }

}
