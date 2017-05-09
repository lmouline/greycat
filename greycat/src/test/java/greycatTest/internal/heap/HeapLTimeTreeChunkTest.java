package greycatTest.internal.heap;

import greycat.internal.heap.HeapChunkSpace;
import greycat.internal.heap.HeapLTimeTreeChunk;
import org.junit.Assert;
import org.junit.Test;

public class HeapLTimeTreeChunkTest {

    @Test
    public void test() {
        HeapChunkSpace space = new HeapChunkSpace(100, null, false);
        HeapLTimeTreeChunk tree = new HeapLTimeTreeChunk(space, -1);

        for (int i = 0; i < 100; i = i + 10) {
            tree.insert(i);
        }
        for (int i = 5; i < 100; i = i + 10) {
            Assert.assertEquals(tree.previous(i), i - 5);
        }

    }

}
