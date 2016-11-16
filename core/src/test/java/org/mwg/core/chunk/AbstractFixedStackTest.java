package org.mwg.core.chunk;

import org.junit.Assert;
import org.mwg.chunk.Stack;

public class AbstractFixedStackTest {

    protected static final int CAPACITY = 15;

    protected void test(Stack stack) {
        // stack is initially full, dequeue until empty
        for (int i = 0; i < CAPACITY; i++) {
            Assert.assertTrue(stack.dequeueTail() == i);
        }
        // insert some values again
        Assert.assertTrue(stack.enqueue(0));
        Assert.assertTrue(stack.enqueue(1));
        Assert.assertTrue(stack.enqueue(2));
        Assert.assertTrue(stack.enqueue(3));
        Assert.assertTrue(stack.enqueue(4));
        // dequeue tail
        Assert.assertTrue(stack.dequeueTail() == 0);
        // enqueue
        Assert.assertTrue(stack.enqueue(5));
        // dequeue index
        Assert.assertTrue(stack.dequeue(2));
        // dequeue tail
        Assert.assertTrue(stack.dequeueTail() == 1);
        Assert.assertTrue(stack.dequeueTail() == 3);
        // dequeue invalid index
        Assert.assertFalse(stack.dequeue(2));
        Assert.assertFalse(stack.dequeue(1));
        Assert.assertFalse(stack.dequeue(0));
        // dequeue valid index
        Assert.assertTrue(stack.dequeue(4));
        // dequeue tail
        Assert.assertTrue(stack.dequeueTail() == 5);

    }
}
