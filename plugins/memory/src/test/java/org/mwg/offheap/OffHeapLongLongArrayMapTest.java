package org.mwg.offheap;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by duke on 04/08/16.
 */
public class OffHeapLongLongArrayMapTest {

    @Test
    public void arrayOffHeapTest() {
        OffHeapByteArray.alloc_counter = 0;
        OffHeapDoubleArray.alloc_counter = 0;
        OffHeapLongArray.alloc_counter = 0;
        OffHeapStringArray.alloc_counter = 0;

        Unsafe.DEBUG_MODE = true;

        org.mwg.core.chunk.offheap.ArrayLongLongArrayMap map = new org.mwg.core.chunk.offheap.ArrayLongLongArrayMap(this, CoreConstants.MAP_INITIAL_CAPACITY, -1);
        org.mwg.core.chunk.offheap.ArrayLongLongArrayMap.incrementCopyOnWriteCounter(map.rootAddress());
        test(map);
        org.mwg.core.chunk.offheap.ArrayLongLongArrayMap.free(map.rootAddress());

        Assert.assertTrue(OffHeapByteArray.alloc_counter == 0);
        Assert.assertTrue(OffHeapDoubleArray.alloc_counter == 0);
        Assert.assertTrue(OffHeapLongArray.alloc_counter == 0);
        Assert.assertTrue(OffHeapStringArray.alloc_counter == 0);
    }

}
