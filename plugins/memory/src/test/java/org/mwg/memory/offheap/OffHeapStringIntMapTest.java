package org.mwg.memory.offheap;

import org.junit.After;
import org.junit.Assert;
import org.mwg.internal.chunk.AbstractStringIntMapTest;

public class OffHeapStringIntMapTest extends AbstractStringIntMapTest {

    public OffHeapStringIntMapTest() {
        super(new OffHeapMemoryFactory());
    }

    @After
    public void tearDown() throws Exception {
        if (OffHeapConstants.DEBUG_MODE) {
            Assert.assertEquals(OffHeapConstants.SEGMENTS.size(), 0);
        }
    }

}
