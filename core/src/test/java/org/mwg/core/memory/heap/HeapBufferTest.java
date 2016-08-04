package org.mwg.core.memory.heap;

import org.mwg.core.memory.AbstractBufferTest;
import org.mwg.core.memory.HeapMemoryFactory;

public class HeapBufferTest extends AbstractBufferTest {

    public HeapBufferTest(){
        super(new HeapMemoryFactory());
    }

}
