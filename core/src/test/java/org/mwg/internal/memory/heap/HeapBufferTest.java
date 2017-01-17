package org.mwg.internal.memory.heap;

import org.mwg.internal.memory.AbstractBufferTest;
import org.mwg.internal.memory.HeapMemoryFactory;

public class HeapBufferTest extends AbstractBufferTest {

    public HeapBufferTest(){
        super(new HeapMemoryFactory());
    }

}
