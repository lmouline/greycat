package org.mwg.structure.tree;

import org.junit.Assert;
import org.junit.Test;
import org.mwg.*;
import org.mwg.structure.StructurePlugin;
import org.mwg.structure.TreeResult;

import java.util.Random;

public class NDTreeTest {
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
                NDTree ndTree = (NDTree) graph.newTypedNode(0, 0, NDTree.NAME);

                int dim = 5;
                double[] precisions = new double[dim];
                double[] boundMin = new double[dim];
                double[] boundMax = new double[dim];
                for (int i = 0; i < dim; i++) {
                    precisions[i] = 0.000001;
                    boundMin[i] = 0;
                    boundMax[i] = 1;
                }
                ndTree.set(NDTree.BOUND_MIN, Type.DOUBLE_ARRAY, boundMin);
                ndTree.set(NDTree.BOUND_MAX, Type.DOUBLE_ARRAY, boundMax);
                ndTree.set(NDTree.RESOLUTION, Type.DOUBLE_ARRAY, precisions);
                Random random = new Random();
                random.setSeed(125362l);
                int ins = 1000;
                int test = 100;
                int nsearch = 10;


                Node[] nodes = new Node[ins];
                double[][] keys = new double[ins][];
                double[][] keysTest = new double[test][];

                for (int i = 0; i < ins; i++) {
                    //temp.setProperty("value", Type.DOUBLE, random.nextDouble());
                    double[] key = new double[dim];
                    for (int j = 0; j < dim; j++) {
                        key[j] = random.nextDouble();
                    }
                    keys[i] = key;
                    nodes[i] = graph.newNode(0, 0);
                }

                for (int i = 0; i < test; i++) {
                    double[] key = new double[dim];
                    for (int j = 0; j < dim; j++) {
                        key[j] = random.nextDouble();
                    }
                    keysTest[i] = key;
                }


                long ts = System.currentTimeMillis();
                for (int i = 0; i < ins; i++) {
                    ndTree.insert(keys[i], nodes[i].id());
                }
                long te = System.currentTimeMillis() - ts;

                System.out.println("NDTree insert: " + te + " ms");

                ts = System.currentTimeMillis();
                for (int i = 0; i < ins; i++) {
                    kdTree.insert(keys[i], nodes[i].id());
                }
                te = System.currentTimeMillis() - ts;

                System.out.println("KDTree insert: " + te + " ms");



                long[][] temp = new long[test][nsearch];
                ts = System.currentTimeMillis();
                for (int i = 0; i < test; i++) {
                    TreeResult res = ndTree.nearestN(keysTest[i], nsearch);
                    for (int j = 0; j < nsearch; j++) {
                        temp[i][j] = res.value(j);
                    }
                    res.free();
                }
                te = System.currentTimeMillis() - ts;
                System.out.println("NDTree get all: " + te + " ms");

                long[][] tempkdtree = new long[test][nsearch];
                ts = System.currentTimeMillis();
                for (int i = 0; i < test; i++) {
                    TreeResult res = kdTree.nearestN(keysTest[i], nsearch);
                    for (int j = 0; j < nsearch; j++) {
                        tempkdtree[i][j] = res.value(j);
                    }
                    res.free();
                }
                te = System.currentTimeMillis() - ts;
                System.out.println("KDTree get all: " + te + " ms");

                for (int i = 0; i < test; i++) {
                    for (int j = 0; j < nsearch; j++) {
                        if (temp[i][j] != tempkdtree[i][j]) {
                            throw new RuntimeException("Error!");
                        }
                    }
                }


                double[] mins=new double[dim];
                double[] maxs=new double[dim];

                for(int i=0;i<dim;i++){
                    mins[i]=0.2;
                    maxs[i]=0.7;
                }

                ts = System.currentTimeMillis();
                TreeResult trangeND= ndTree.query(mins,maxs);
                te = System.currentTimeMillis() - ts;
                System.out.println("NDtree range: " + te + " ms");

                ts = System.currentTimeMillis();
                TreeResult trangeKD = kdTree.query(mins,maxs);
                te = System.currentTimeMillis() - ts;
                System.out.println("KDTree range: " + te + " ms");

                Assert.assertTrue(trangeKD.size()==trangeND.size());


                for(int i=0;i<trangeKD.size();i++){
                    Assert.assertTrue(trangeKD.value(i)==trangeND.value(i));
                }


                System.out.println("test pass!");

            }
        });
    }

}
