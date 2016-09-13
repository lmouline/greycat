package org.mwg.structure;

import org.junit.Assert;
import org.junit.Test;
import org.mwg.*;
import org.mwg.structure.tree.NDTree;

import java.util.Random;

/**
 * Created by assaad on 30/08/16.
 */

public class NDTreeTest {
    @Test
    public void NDInsertTest() {
        final Graph graph = new GraphBuilder()
                .withPlugin(new StructurePlugin())
                //.withScheduler(new NoopScheduler())
                .withMemorySize(100000)
                //.withOffHeapMemory()
                .build();
        graph.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean result) {
                NDTree testtree = (NDTree) graph.newTypedNode(0, 0, NDTree.NAME);

                //Day - Hours - Temperature - Power
             /*   double[] precisions = {1, 0.25, 1, 50};
                double[] boundMin = {0, 0, -10, 0};
                double[] boundMax = {6, 24, 30, 3000};*/

                double[] precisions = {0.2, 0.2};
                double[] boundMin = {0, 0};
                double[] boundMax = {1, 1};


                testtree.setProperty(NDTree.BOUNDMIN, Type.DOUBLE_ARRAY, boundMin);
                testtree.setProperty(NDTree.BOUNDMAX, Type.DOUBLE_ARRAY, boundMax);
                testtree.setProperty(NDTree.PRECISION, Type.DOUBLE_ARRAY, precisions);

                Random random = new Random();
                random.setSeed(125362l);
                int ins = 100;

                graph.save(null);
                long initcache = graph.space().available();

                for (int i = 0; i < ins; i++) {
                    Node temp = graph.newNode(0, 0);
                    temp.setProperty("value", Type.DOUBLE, random.nextDouble());

                    double[] key = {random.nextDouble(), random.nextDouble()};
                    testtree.insert(key, temp, null);
                    temp.free();
                }


                graph.save(null);
                Assert.assertTrue(graph.space().available() == initcache);


                double[] res={0.1,0.35};




            }
        });
    }
}
