package org.mwg.memory.offheap;

import org.junit.After;
import org.junit.Assert;
import org.mwg.core.chunk.AbstractTimeTreeTest;

public class OffHeapTimeTreeChunkTest extends AbstractTimeTreeTest {

    public OffHeapTimeTreeChunkTest() {
        super(new OffHeapMemoryFactory());
    }

    @After
    public void tearDown() throws Exception {
        if (OffHeapConstants.DEBUG_MODE) {
            Assert.assertEquals(OffHeapConstants.SEGMENTS.size(),0);
        }
    }

}
