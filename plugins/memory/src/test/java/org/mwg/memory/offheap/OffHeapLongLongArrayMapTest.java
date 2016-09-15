package org.mwg.memory.offheap;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.mwg.core.chunk.AbstractLongLongArrayMapTest;
import org.mwg.memory.offheap.primary.OffHeapByteArray;
import org.mwg.memory.offheap.primary.OffHeapDoubleArray;
import org.mwg.memory.offheap.primary.OffHeapLongArray;
import org.mwg.memory.offheap.primary.OffHeapString;
import org.mwg.utility.Unsafe;

public class OffHeapLongLongArrayMapTest extends AbstractLongLongArrayMapTest {

    public OffHeapLongLongArrayMapTest() {
        super(new OffHeapMemoryFactory());
    }

    @After
    public void tearDown() throws Exception {
        if (OffHeapConstants.DEBUG_MODE) {
            Assert.assertEquals(OffHeapConstants.SEGMENTS.size(),0);
        }
    }

}
