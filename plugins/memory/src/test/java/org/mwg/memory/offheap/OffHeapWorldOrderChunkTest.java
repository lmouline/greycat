package org.mwg.memory.offheap;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.mwg.core.chunk.AbstractWorldOrderChunkTest;
import org.mwg.utility.Unsafe;

public class OffHeapWorldOrderChunkTest extends AbstractWorldOrderChunkTest {

    public OffHeapWorldOrderChunkTest() {
        super(new OffHeapMemoryFactory());
    }

    @Before
    public void setUp() throws Exception {
        OffHeapByteArray.alloc_counter = 0;
        OffHeapDoubleArray.alloc_counter = 0;
        OffHeapLongArray.alloc_counter = 0;
        OffHeapStringArray.alloc_counter = 0;
        Unsafe.DEBUG_MODE = true;
    }

    @After
    public void tearDown() throws Exception {
        Assert.assertTrue(OffHeapByteArray.alloc_counter == 0);
        Assert.assertTrue(OffHeapDoubleArray.alloc_counter == 0);
        Assert.assertTrue(OffHeapLongArray.alloc_counter == 0);
        Assert.assertTrue(OffHeapStringArray.alloc_counter == 0);
    }

}
