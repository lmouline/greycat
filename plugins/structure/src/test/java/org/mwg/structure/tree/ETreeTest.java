package org.mwg.structure.tree;

import org.junit.Test;
import org.mwg.*;
import org.mwg.structure.StructurePlugin;

import java.util.Random;

/**
 * Created by assaad on 19/12/2016.
 */
public class ETreeTest {
    @Test
    public void NDTest() {
        final Graph graph = new GraphBuilder()
                .withPlugin(new StructurePlugin())
                .withMemorySize(10000)
                .build();
        graph.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean result) {
                ETree eTree= (ETree) graph.newTypedNode(0,0,ETree.NAME);

                int dim = 5;

                double[] precisions = new double[dim];
                double[] boundMin = new double[dim];
                double[] boundMax = new double[dim];

                for (int i = 0; i < dim; i++) {
                    precisions[i] = 0.1;
                    boundMin[i] = 0;
                    boundMax[i] = 1;
                }


                eTree.set(ETree.BOUND_MIN, Type.DOUBLE_ARRAY, boundMin);
                eTree.set(ETree.BOUND_MAX, Type.DOUBLE_ARRAY, boundMax);
                eTree.set(ETree.RESOLUTION, Type.DOUBLE_ARRAY, precisions);

                Random random = new Random();
                random.setSeed(125362l);
                int ins = 10000;


                double[][] keys = new double[ins][];


                for (int i = 0; i < ins; i++) {
                    //temp.setProperty("value", Type.DOUBLE, random.nextDouble());

                    double[] key = new double[dim];
                    for (int j = 0; j < dim; j++) {
                        key[j] = random.nextDouble();
                    }

                    keys[i] = key;
                }

                long ts=System.currentTimeMillis();
                for (int i = 0; i < ins; i++) {
                    eTree.insertWith(keys[i], null, null);
                }
                long te=System.currentTimeMillis()-ts;
                System.out.println("insert "+ins+" points in: "+te+" ms, created: "+ETree.counter+" subnodes");




            }
        });
    }

}
