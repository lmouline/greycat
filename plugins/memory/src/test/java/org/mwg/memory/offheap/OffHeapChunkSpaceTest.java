package org.mwg.memory.offheap;

import org.junit.After;
import org.junit.Assert;
import org.mwg.internal.chunk.AbstractChunkSpaceTest;

public class OffHeapChunkSpaceTest extends AbstractChunkSpaceTest {

    public OffHeapChunkSpaceTest() {
        super(new OffHeapMemoryFactory());
    }

    @After
    public void tearDown() throws Exception {
        if (OffHeapConstants.DEBUG_MODE) {
            Assert.assertEquals(OffHeapConstants.SEGMENTS.size(),0);
        }
    }

}
