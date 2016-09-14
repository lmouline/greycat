package org.mwg.structure.tree;

import org.junit.Assert;
import org.junit.Test;
import org.mwg.*;
import org.mwg.structure.KDTreeJava;
import org.mwg.structure.StructurePlugin;
import org.mwg.structure.distance.Distances;
import org.mwg.structure.distance.EuclideanDistance;
import org.mwg.structure.distance.GeoDistance;
import org.mwg.structure.tree.NDTree;

import java.util.Random;

/**
 * Created by assaad on 30/08/16.
 */

public class NDTreeTest {

    @Test
    public void NDTest() {
        final Graph graph = new GraphBuilder()
                .withPlugin(new StructurePlugin())
                .withMemorySize(1000)
                //.withOffHeapMemory()
                .build();
        graph.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean result) {
                NDTree ndTree = (NDTree) graph.newTypedNode(0, 0, NDTree.NAME);
                KDTree kdtree = (KDTree) graph.newTypedNode(0, 0, KDTree.NAME);

                KDTreeJava kdtreejava = new KDTreeJava();
                kdtreejava.setThreshold(KDTree.DISTANCE_THRESHOLD_DEF);

                boolean print = true;


                kdtreejava.setDistance(GeoDistance.instance());
                kdtree.setDistance(Distances.GEODISTANCE);
                ndTree.setDistance(Distances.GEODISTANCE);

                int dim = 2;

                double[] precisions = new double[dim];
                double[] boundMin = new double[dim];
                double[] boundMax = new double[dim];

                for (int i = 0; i < dim; i++) {
                    precisions[i] = 0.25;
                    boundMin[i] = 0;
                    boundMax[i] = 1;
                }


                ndTree.setProperty(NDTree.BOUNDMIN, Type.DOUBLE_ARRAY, boundMin);
                ndTree.setProperty(NDTree.BOUNDMAX, Type.DOUBLE_ARRAY, boundMax);
                ndTree.setProperty(NDTree.PRECISION, Type.DOUBLE_ARRAY, precisions);


                Random random = new Random();
                random.setSeed(125362l);
                int ins = 100;

                graph.save(null);
                long initcache = graph.space().available();


                double[][] keys = new double[ins][];
                Node[] values = new Node[ins];


                for (int i = 0; i < ins; i++) {
                    Node temp = graph.newNode(0, 0);
                    //temp.setProperty("value", Type.DOUBLE, random.nextDouble());

                    double[] key = new double[dim];
                    for (int j = 0; j < dim; j++) {
                        key[j] = random.nextDouble();
                    }

                    temp.set("key", key);
                    keys[i] = key;
                    values[i] = temp;
                }

                for (int i = 0; i < ins; i++) {
                    ndTree.insertWith(keys[i], values[i], null);
                    kdtree.insertWith(keys[i], values[i], null);
                    kdtreejava.insert(keys[i], values[i], null);
                }


                for (int j = 0; j < ins; j++) {

                    final double[] res = keys[j];
                    ndTree.nearestN(res, 10, new Callback<Node[]>() {
                        @Override
                        public void on(Node[] result) {
                            kdtree.nearestN(res, 10, new Callback<Node[]>() {
                                @Override
                                public void on(Node[] result2) {
                                    for (int i = 0; i < result.length; i++) {
                                        Assert.assertTrue(result[i].id() == result2[i].id());
                                    }
                                }
                            });

                        }
                    });
                }


            }
        });
    }


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
                NDTree ndTree = (NDTree) graph.newTypedNode(0, 0, NDTree.NAME);
                KDTree kdtree = (KDTree) graph.newTypedNode(0, 0, KDTree.NAME);

                KDTreeJava kdtreejava = new KDTreeJava();
                kdtreejava.setThreshold(KDTree.DISTANCE_THRESHOLD_DEF);

                boolean print = true;


//                kdtreejava.setDistance(EuclideanDistance.instance());
//                kdtree.setDistance(Distances.EUCLIDEAN);
//                ndTree.setDistance(Distances.EUCLIDEAN);


                kdtreejava.setDistance(GeoDistance.instance());
                kdtree.setDistance(Distances.GEODISTANCE);
                ndTree.setDistance(Distances.GEODISTANCE);

                //Day - Hours - Temperature - Power
             /*   double[] precisions = {1, 0.25, 1, 50};
                double[] boundMin = {0, 0, -10, 0};
                double[] boundMax = {6, 24, 30, 3000};*/

                int dim = 3;

                double[] precisions = new double[dim];
                double[] boundMin = new double[dim];
                double[] boundMax = new double[dim];

                for (int i = 0; i < dim; i++) {
                    precisions[i] = 0.5;
                    boundMin[i] = 0;
                    boundMax[i] = 1;
                }


                ndTree.setProperty(NDTree.BOUNDMIN, Type.DOUBLE_ARRAY, boundMin);
                ndTree.setProperty(NDTree.BOUNDMAX, Type.DOUBLE_ARRAY, boundMax);
                ndTree.setProperty(NDTree.PRECISION, Type.DOUBLE_ARRAY, precisions);


                Random random = new Random();
                random.setSeed(125362l);
                int ins = 1000;

                graph.save(null);
                long initcache = graph.space().available();


                double[][] keys = new double[ins][];
                Node[] values = new Node[ins];


                for (int i = 0; i < ins; i++) {
                    Node temp = graph.newNode(0, 0);
                    //temp.setProperty("value", Type.DOUBLE, random.nextDouble());

                    double[] key = new double[dim];
                    for (int j = 0; j < dim; j++) {
                        key[j] = random.nextDouble();
                    }

                    temp.set("key", key);
                    keys[i] = key;
                    values[i] = temp;
                }

                long starttime = System.currentTimeMillis();
                for (int i = 0; i < ins; i++) {
                    ndTree.insertWith(keys[i], values[i], null);
                }
                long endtime = System.currentTimeMillis();
                double exectime = endtime - starttime;
                System.out.println("Nd tree insert: " + exectime + " ms");


                starttime = System.currentTimeMillis();
                for (int i = 0; i < ins; i++) {
                    kdtree.insertWith(keys[i], values[i], null);
                }
                endtime = System.currentTimeMillis();
                exectime = endtime - starttime;
                System.out.println("kd tree insert: " + exectime + " ms");


                starttime = System.currentTimeMillis();
                for (int i = 0; i < ins; i++) {
                    kdtreejava.insert(keys[i], values[i], null);
                }
                endtime = System.currentTimeMillis();
                exectime = endtime - starttime;
                System.out.println("kd tree java insert: " + exectime + " ms");


//                graph.save(null);
//                Assert.assertTrue(graph.space().available() == initcache);


                double[] res = new double[dim];
                for (int j = 0; j < dim; j++) {
                    res[j] = j * (1.0 / dim);
                }

                System.out.println("ND TREE");

                starttime = System.currentTimeMillis();
                ndTree.nearestN(res, 10, new Callback<Node[]>() {
                    @Override
                    public void on(Node[] result) {
                        if (print) {
                            for (int i = 0; i < result.length; i++) {
                                System.out.println(result[i] + " dist: " + EuclideanDistance.instance().measure(res, (double[]) result[i].get("key")));
                            }
                        }
                    }
                });
                endtime = System.currentTimeMillis();
                exectime = endtime - starttime;
                System.out.println("nd tree search: " + exectime + " ms");


                System.out.println("");
                System.out.println("KD TREE");

                starttime = System.currentTimeMillis();
                kdtree.nearestN(res, 10, new Callback<Node[]>() {
                    @Override
                    public void on(Node[] result) {
                        if (print) {
                            for (int i = 0; i < result.length; i++) {
                                System.out.println(result[i] + " dist: " + EuclideanDistance.instance().measure(res, (double[]) result[i].get("key")));
                            }
                        }

                    }
                });
                endtime = System.currentTimeMillis();
                exectime = endtime - starttime;
                System.out.println("kd tree search: " + exectime + " ms");

                System.out.println("");
                System.out.println("KD TREE java");


                starttime = System.currentTimeMillis();
                kdtreejava.nearestN(res, 10, new Callback<Object[]>() {
                    @Override
                    public void on(Object[] result) {
                        if (print) {
                            for (int i = 0; i < result.length; i++) {
                                System.out.println(result[i] + " dist: " + EuclideanDistance.instance().measure(res, (double[]) ((Node) result[i]).get("key")));
                            }
                        }

                    }
                });
                endtime = System.currentTimeMillis();
                exectime = endtime - starttime;
                System.out.println("kd tree java search: " + exectime + " ms");
                System.out.println("");


                starttime = System.currentTimeMillis();
                for (int i = 0; i < ins; i++) {
                    ndTree.nearestN(keys[i], 10, new Callback<Node[]>() {
                        @Override
                        public void on(Node[] result) {
                        }
                    });
                }
                endtime = System.currentTimeMillis();
                exectime = endtime - starttime;
                System.out.println("nd tree all search: " + exectime + " ms");


                starttime = System.currentTimeMillis();
                for (int i = 0; i < ins; i++) {
                    kdtree.nearestN(keys[i], 10, new Callback<Node[]>() {
                        @Override
                        public void on(Node[] result) {
                        }
                    });
                }
                endtime = System.currentTimeMillis();
                exectime = endtime - starttime;
                System.out.println("kd tree all search: " + exectime + " ms");


                starttime = System.currentTimeMillis();
                for (int i = 0; i < ins; i++) {
                    kdtreejava.nearestN(keys[i], 10, new Callback<Object[]>() {
                        @Override
                        public void on(Object[] result) {
                        }
                    });
                }
                endtime = System.currentTimeMillis();
                exectime = endtime - starttime;
                System.out.println("kd tree java all search: " + exectime + " ms");

            }
        });
    }
}
