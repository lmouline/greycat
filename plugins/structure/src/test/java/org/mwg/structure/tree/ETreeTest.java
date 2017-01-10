package org.mwg.structure.tree;

import org.junit.Assert;
import org.junit.Test;
import org.mwg.*;
import org.mwg.structure.StructurePlugin;
import org.mwg.structure.TreeResult;

import java.util.Random;

public class ETreeTest {
    @Test
    public void NDTest() {
        final Graph graph = new GraphBuilder()
                .withPlugin(new StructurePlugin())
                .withMemorySize(1000000)
                .build();
        graph.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean result) {

                KDTree kdTree = (KDTree) graph.newTypedNode(0, 0, KDTree.NAME);
                ETree eTree = (ETree) graph.newTypedNode(0, 0, ETree.NAME);

                int dim = 5;
                double[] precisions = new double[dim];
                double[] boundMin = new double[dim];
                double[] boundMax = new double[dim];
                for (int i = 0; i < dim; i++) {
                    precisions[i] = 0.000001;
                    boundMin[i] = 0;
                    boundMax[i] = 1;
                }
                eTree.setAt(ETree.BOUND_MIN, Type.DOUBLE_ARRAY, boundMin);
                eTree.setAt(ETree.BOUND_MAX, Type.DOUBLE_ARRAY, boundMax);
                eTree.setAt(ETree.RESOLUTION, Type.DOUBLE_ARRAY, precisions);
                Random random = new Random();
                random.setSeed(125362l);
                int ins = 100;
                int nsearch = 3;


                Node[] nodes = new Node[ins];
                double[][] keys = new double[ins][];
                for (int i = 0; i < ins; i++) {
                    //temp.setProperty("value", Type.DOUBLE, random.nextDouble());
                    double[] key = new double[dim];
                    for (int j = 0; j < dim; j++) {
                        key[j] = random.nextDouble();
                    }
                    keys[i] = key;
                    nodes[i] = graph.newNode(0, 0);
                }

                long ts = System.currentTimeMillis();
                for (int i = 0; i < ins; i++) {
                    eTree.insert(keys[i], nodes[i].id());
                }
                long te = System.currentTimeMillis() - ts;

                System.out.println("ETree insert: " + te + " ms");

                ts = System.currentTimeMillis();
                for (int i = 0; i < ins; i++) {
                    kdTree.insertWith(keys[i], nodes[i], null);
                }
                te = System.currentTimeMillis() - ts;

                System.out.println("kdTree insert: " + te + " ms");


                System.out.println(eTree.numberOfNodes());
                Assert.assertEquals(eTree.size(),ins);
                Assert.assertEquals(kdTree.size(),ins);

                long[][] temp = new long[ins][nsearch];
                ts = System.currentTimeMillis();
                for (int i = 0; i < ins; i++) {
                    TreeResult res = eTree.nearestN(keys[i], nsearch);
                    for (int j = 0; j < nsearch; j++) {
                        temp[i][j] = res.value(j);
                    }
                    res.free();
                }
                te = System.currentTimeMillis() - ts;
                System.out.println("eTree get all: " + te + " ms");

                long[][] tempkdtree = new long[ins][nsearch];
                ts = System.currentTimeMillis();
                for (int i = 0; i < ins; i++) {
                    int finalI = i;
                    kdTree.nearestN(keys[i], nsearch, new Callback<Node[]>() {
                        @Override
                        public void on(Node[] result) {
                            for (int j = 0; j < nsearch; j++) {
                                tempkdtree[finalI][j] = result[j].id();
                            }
                        }
                    });
                }
                te = System.currentTimeMillis() - ts;
                System.out.println("kdTree get all: " + te + " ms");

                for (int i = 0; i < ins; i++) {
                    for (int j = 0; j < nsearch; j++) {
                        if (temp[i][j] != tempkdtree[i][j]) {
                            throw new RuntimeException("Error! "+temp[i][j]+"!="+tempkdtree[i][j]);
                        }
                    }
                }
                System.out.println("test pass!");
            }
        });
    }

}
