package org.mwg.memory.offheap;

import org.mwg.core.memory.AbstractBufferTest;

public class OffHeapBufferTest extends AbstractBufferTest {

    public OffHeapBufferTest() {
        super(new OffHeapMemoryFactory());
    }

}
