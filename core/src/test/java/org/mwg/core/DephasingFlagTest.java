package org.mwg.core;

import org.junit.Assert;
import org.junit.Test;
import org.mwg.Callback;
import org.mwg.Graph;
import org.mwg.GraphBuilder;
import org.mwg.Node;
import org.mwg.core.chunk.heap.HeapChunkSpace;
import org.mwg.core.chunk.offheap.OffHeapByteArray;
import org.mwg.core.chunk.offheap.OffHeapDoubleArray;
import org.mwg.core.chunk.offheap.OffHeapLongArray;
import org.mwg.core.chunk.offheap.OffHeapStringArray;
import org.mwg.core.scheduler.NoopScheduler;
import org.mwg.core.utility.Unsafe;
import org.mwg.plugin.NodeState;

public class DephasingFlagTest {

    private long cacheSize = 10000;

    @Test
    public void heapTest() {
        dephaseFlagTest(new GraphBuilder().withMemorySize(cacheSize).saveEvery(cacheSize - 100).withScheduler(new NoopScheduler()).build());
    }

    /**
     * @ignore ts
     */
    @Test
    public void offHeapTest() {
        OffHeapByteArray.alloc_counter = 0;
        OffHeapDoubleArray.alloc_counter = 0;
        OffHeapLongArray.alloc_counter = 0;
        OffHeapStringArray.alloc_counter = 0;

        Unsafe.DEBUG_MODE = true;

        dephaseFlagTest(new GraphBuilder().withScheduler(new NoopScheduler()).withOffHeapMemory().withMemorySize(cacheSize).saveEvery(cacheSize - 100).build());

        Assert.assertTrue(OffHeapByteArray.alloc_counter == 0);
        Assert.assertTrue(OffHeapDoubleArray.alloc_counter == 0);
        Assert.assertTrue(OffHeapLongArray.alloc_counter == 0);
        Assert.assertTrue(OffHeapStringArray.alloc_counter == 0);
    }

    private void dephaseFlagTest(final Graph graph) {


        graph.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean result) {

                long init = graph.space().available();

                Node n0 = graph.newNode(0, 0);
                n0.set("name", "n0");

                n0.jump(5, new Callback<Node>() {
                    @Override
                    public void on(Node n5) {
                        NodeState dephasedN5 = graph.resolver().newState(n5, 0, 3);
                        //n5.set("name", "n5");
                        n5.free();
                    }
                });

                n0.free();


                graph.save(new Callback<Boolean>() {
                    @Override
                    public void on(Boolean result) {
                        long nb = graph.space().available();

                        /*
                        if(graph.space() instanceof HeapChunkSpace){
                            ((HeapChunkSpace) graph.space()).printMarked();
                        }*/

                        Assert.assertEquals(init, nb);
                    }
                });

                graph.disconnect(new Callback<Boolean>() {
                    @Override
                    public void on(Boolean result) {

                    }
                });

            }
        });
    }

}
