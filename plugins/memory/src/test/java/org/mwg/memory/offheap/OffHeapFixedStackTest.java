package org.mwg.memory.offheap;

import org.junit.Assert;
import org.junit.Test;
import org.mwg.chunk.Stack;
import org.mwg.core.chunk.AbstractFixedStackTest;
import org.mwg.memory.offheap.primary.OffHeapByteArray;
import org.mwg.memory.offheap.primary.OffHeapLongArray;
import org.mwg.utility.Unsafe;

public class OffHeapFixedStackTest extends AbstractFixedStackTest {

    @Test
    public void offHeapFixedStackTest() {

        Stack stack = new OffHeapFixedStack(CAPACITY, true);
        test(stack);
        stack.free();

        if (OffHeapConstants.DEBUG_MODE) {
            Assert.assertEquals(OffHeapConstants.SEGMENTS.size(), 1);
        }

    }

}
