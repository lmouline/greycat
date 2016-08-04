package org.mwg.offheap;

import org.junit.Assert;
import org.junit.Test;
import org.mwg.chunk.WorldOrderChunk;
import org.mwg.struct.Buffer;
import sun.misc.Unsafe;

public class OffHeapWorldOrderChunkTest {

    /**
     * @ignore ts
     */
    @Test
    public void offHeapTest() {

        OffHeapByteArray.alloc_counter = 0;
        OffHeapDoubleArray.alloc_counter = 0;
        OffHeapLongArray.alloc_counter = 0;
        OffHeapStringArray.alloc_counter = 0;

        Unsafe.DEBUG_MODE = true;

        final ChunkListener selfPointer = this;
        WorldOrderChunkFactory factory = new WorldOrderChunkFactory() {

            @Override
            public WorldOrderChunk create(Buffer initialPayload) {
                return new OffHeapWorldOrderChunk(selfPointer, OffHeapConstants.OFFHEAP_NULL_PTR, initialPayload);
            }
        };
        orderTest(factory);
        saveLoadTest(factory);

        Assert.assertTrue(OffHeapByteArray.alloc_counter == 0);
        Assert.assertTrue(OffHeapDoubleArray.alloc_counter == 0);
        Assert.assertTrue(OffHeapLongArray.alloc_counter == 0);
        Assert.assertTrue(OffHeapStringArray.alloc_counter == 0);

    }

}
