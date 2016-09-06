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

        OffHeapByteArray.alloc_counter = 0;
        OffHeapLongArray.alloc_counter = 0;

        Unsafe.DEBUG_MODE = true;

        Stack stack = new OffHeapFixedStack(CAPACITY, true);
        test(stack);
        stack.free();

        Assert.assertTrue(OffHeapByteArray.alloc_counter == 0);
        Assert.assertTrue(OffHeapLongArray.alloc_counter == 0);

    }

}
