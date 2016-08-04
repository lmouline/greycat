package org.mwg.offheap;

import org.junit.Assert;
import org.junit.Test;
import org.mwg.chunk.GenChunk;
import org.mwg.core.chunk.GenChunkTest;
import org.mwg.struct.Buffer;

public class OffHeapGenChunkTest {

    @Test
    public void offHeapTest() {

        OffHeapByteArray.alloc_counter = 0;
        OffHeapDoubleArray.alloc_counter = 0;
        OffHeapLongArray.alloc_counter = 0;
        OffHeapStringArray.alloc_counter = 0;

        Unsafe.DEBUG_MODE = true;

        final ChunkListener selfPointer = this;
        GenChunkTest.GenChunkFactory factory = new GenChunkTest.GenChunkFactory() {
            @Override
            public GenChunk create(long id, Buffer payload) {
                OffHeapGenChunk newly = new OffHeapGenChunk(selfPointer, org.mwg.core.CoreConstants.OFFHEAP_NULL_PTR, payload);
                OffHeapLongArray.set(newly.addr(), CoreConstants.OFFHEAP_CHUNK_INDEX_ID, id);
                return newly;
            }
        };

        genTest(factory);

        Assert.assertTrue(OffHeapByteArray.alloc_counter == 0);
        Assert.assertTrue(OffHeapDoubleArray.alloc_counter == 0);
        Assert.assertTrue(OffHeapLongArray.alloc_counter == 0);
        Assert.assertTrue(OffHeapStringArray.alloc_counter == 0);

    }

}
