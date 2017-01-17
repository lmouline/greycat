package org.mwg.memory.offheap;

import org.junit.Assert;
import org.junit.Test;
import org.mwg.chunk.Stack;
import org.mwg.internal.chunk.AbstractFixedStackTest;

public class OffHeapFixedStackTest extends AbstractFixedStackTest {

    @Test
    public void offHeapFixedStackTest() {

        Stack stack = new OffHeapFixedStack(CAPACITY, true);
        test(stack);
        stack.free();

        if (OffHeapConstants.DEBUG_MODE) {
            Assert.assertEquals(OffHeapConstants.SEGMENTS.size(), 0);
        }

    }

}
