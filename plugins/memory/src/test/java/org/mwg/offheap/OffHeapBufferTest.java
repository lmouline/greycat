package org.mwg.offheap;

import org.junit.Test;

/**
 * Created by duke on 04/08/16.
 */
public class OffHeapBufferTest {

    @Test
    public void testWriteAllOffHeap() {
        org.mwg.struct.Buffer buffer = BufferBuilder.newOffHeapBuffer();
        testWriteAll(buffer);
    }

    @Test
    public void testIteratorOffHeap() {
        org.mwg.struct.Buffer buffer = BufferBuilder.newOffHeapBuffer();
        testIterator(buffer);
        buffer.free();
        buffer = BufferBuilder.newOffHeapBuffer();
        testIteratorNull(buffer);
        buffer.free();
    }

    @Test
    public void testIteratorOffHeap2() {
        testIterator2(BufferBuilder.newOffHeapBuffer());
    }

    @Test
    public void testOneElementBufferOffHeap() {
        testOneElementBuffer(BufferBuilder.newOffHeapBuffer());
    }

    @Test
    public void testEmptyBufferOffHeap() {
        testEmptyBuffer(BufferBuilder.newOffHeapBuffer());
    }

    @Test
    public void testReadOffHeap() {
        testRead(BufferBuilder.newOffHeapBuffer());
    }

}
