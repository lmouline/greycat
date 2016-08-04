package org.mwg.offheap;

import org.junit.Assert;
import org.junit.Test;

public class OffHeapLongLongMapTest {

    /**
     * @ignore ts
     */
    @Test
    public void arrayOffHeapTest() {
        OffHeapByteArray.alloc_counter = 0;
        OffHeapDoubleArray.alloc_counter = 0;
        OffHeapLongArray.alloc_counter = 0;
        OffHeapStringArray.alloc_counter = 0;

        Unsafe.DEBUG_MODE = true;

        org.mwg.core.chunk.offheap.ArrayLongLongMap map = new org.mwg.core.chunk.offheap.ArrayLongLongMap(this, CoreConstants.MAP_INITIAL_CAPACITY, -1);
        org.mwg.core.chunk.offheap.ArrayLongLongMap.incrementCopyOnWriteCounter(map.rootAddress());
        test(map);
        org.mwg.core.chunk.offheap.ArrayLongLongMap.free(map.rootAddress());

        Assert.assertTrue(OffHeapByteArray.alloc_counter == 0);
        Assert.assertTrue(OffHeapDoubleArray.alloc_counter == 0);
        Assert.assertTrue(OffHeapLongArray.alloc_counter == 0);
        Assert.assertTrue(OffHeapStringArray.alloc_counter == 0);
    }

}
