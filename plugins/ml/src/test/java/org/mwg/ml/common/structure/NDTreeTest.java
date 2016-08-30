package org.mwg.ml.common.structure;

import org.junit.Test;
import org.mwg.*;
import org.mwg.ml.MLPlugin;

/**
 * Created by assaad on 30/08/16.
 */

public class NDTreeTest {
    @Test
    public void NDInsertTest() {
        final Graph graph = new GraphBuilder()
                .withPlugin(new MLPlugin())
                //.withScheduler(new NoopScheduler())
                .withMemorySize(100000)
                //.withOffHeapMemory()
                .build();
        graph.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean result) {
                NDTree testtree = (NDTree) graph.newTypedNode(0, 0, NDTree.NAME);

                //Day - Hours - Temperature - Power
                double[] precisions = {1, 0.25, 1, 50};
                double[] boundMin = {0, 0, -10, 0};
                double[] boundMax = {6, 24, 30, 3000};

                testtree.setProperty(NDTree.BOUNDMIN, Type.DOUBLE_ARRAY, boundMin);
                testtree.setProperty(NDTree.BOUNDMAX, Type.DOUBLE_ARRAY, boundMax);
                testtree.setProperty(NDTree.PRECISION, Type.DOUBLE_ARRAY, precisions);

                Node temp= graph.newNode(0,0);
                temp.setProperty("value",Type.DOUBLE,1.5);

                double[] key={0,12,5,560};
                testtree.insert(key, temp, new Callback<Boolean>() {
                    @Override
                    public void on(Boolean result) {
                        int x=0;
                    }
                });


            }
        });
    }
}
