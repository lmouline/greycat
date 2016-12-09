package org.mwg.structure.tree;

import org.junit.Assert;
import org.junit.Test;
import org.mwg.*;
import org.mwg.core.task.Actions;
import org.mwg.structure.KDTreeJava;
import org.mwg.structure.StructurePlugin;
import org.mwg.structure.action.TraverseById;
import org.mwg.structure.distance.Distances;
import org.mwg.structure.distance.EuclideanDistance;
import org.mwg.structure.distance.GeoDistance;
import org.mwg.task.Task;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;

import java.util.Random;

import static org.mwg.core.task.Actions.*;


public class NDTreeTest {

    private static void printArray(double[] key) {
        System.out.print("[");
        for (int i = 0; i < key.length; i++) {
            System.out.print(key[i]);
            if (i != (key.length - 1)) {
                System.out.print(",");
            }
        }
        System.out.print("] ");
    }

    private static void printgraph(Node root, String name) {
        Task printer = newTask();

        printer
                .then(defineAsVar("parent"))
                .loop("0", "{{tabs}}", newTask().then(Actions.print("\t")))
//                .then(new Action() {
//                    @Override
//                    public void eval(TaskContext context) {
//                        String name=(String) context.variable("name").get(0);
//                        TaskResult res=context.result();
//                        if(res.get(0) instanceof NTree){
//                            Node temp= (Node) res.get(0);
//
//                            System.out.print(name+" - id: "+temp.id()+", min: ");
//                            double[] min=(double[]) temp.getByIndex(6);
//                            double[] max=(double[]) temp.getByIndex(7);
//                            printArray(min);
//                            System.out.print("max: ");
//                            printArray(max);
//
//                            if(temp instanceof SparseNDTree){
//                                Object o=temp.getByIndex(10);
//                                if(o!=null) {
//                                    System.out.print((boolean) temp.getByIndex(10));
//                                }
//                                else{
//                                    System.out.print("false");
//                                }
//                            }
//
//                            System.out.println();
//
//                        }
//                        else{
//
//                            Node temp= (Node) res.get(0);
//                            System.out.print("FINALNODE - id: "+temp.id()+", key: ");
//                            double[] key=(double[]) temp.get("key");
//                            printArray(key);
//                            System.out.println();
//
//                        }
//
//                        context.continueTask();
//                    }
//                })
                .then(println("{{result}}"))
                .then(readVar("tabs"))
                .then(executeExpression("tabs+1"))
                .then(defineAsVar("tabs"))
                .then(readVar("parent"))
                .then(attributesWithTypes(Type.RELATION))
                .forEach(newTask().then(defineAsVar("traverseID")).then(readVar("parent")).then(action(TraverseById.NAME, "{{traverseID}}")).forEach(newTask().isolate(printer)));

        TaskContext tc = printer.prepare(root.graph(), root, new Callback<TaskResult>() {
            @Override
            public void on(TaskResult result) {
                System.out.println("--");
                result.free();
            }
        });

        tc.defineVariable("tabs", -1);
        tc.defineVariable("name", name);

        printer.executeUsing(tc);


    }

    @Test
    public void NDTest() {
        final Graph graph = new GraphBuilder()
                .withPlugin(new StructurePlugin())
                .withMemorySize(10000)
                //.withOffHeapMemory()
                .build();
        graph.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean result) {
                NDTree ndTree = (NDTree) graph.newTypedNode(0, 0, NDTree.NAME);
                KDTree kdtree = (KDTree) graph.newTypedNode(0, 0, KDTree.NAME);
                SparseNDTree sparseNdTree = (SparseNDTree) graph.newTypedNode(0, 0, SparseNDTree.NAME);

                KDTreeJava kdtreejava = new KDTreeJava();
                kdtreejava.setThreshold(KDTree.DISTANCE_THRESHOLD_DEF);

                boolean print = true;


                kdtreejava.setDistance(GeoDistance.instance());
                kdtree.setDistance(Distances.GEODISTANCE);
                ndTree.setDistance(Distances.GEODISTANCE);
                sparseNdTree.setDistance(Distances.GEODISTANCE);

                int dim = 2;

                double[] precisions = new double[dim];
                double[] boundMin = new double[dim];
                double[] boundMax = new double[dim];

                for (int i = 0; i < dim; i++) {
                    precisions[i] = 0.25;
                    boundMin[i] = 0;
                    boundMax[i] = 1;
                }


                ndTree.setAt(NDTree.BOUNDMIN, Type.DOUBLE_ARRAY, boundMin);
                ndTree.setAt(NDTree.BOUNDMAX, Type.DOUBLE_ARRAY, boundMax);
                ndTree.setAt(NDTree.PRECISION, Type.DOUBLE_ARRAY, precisions);

                sparseNdTree.setBounds(boundMin, boundMax);


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

                    temp.set("key", Type.DOUBLE_ARRAY, key);
                    keys[i] = key;
                    values[i] = temp;
                }

                for (int i = 0; i < ins; i++) {
                    ndTree.insertWith(keys[i], values[i], null);
                    kdtree.insertWith(keys[i], values[i], null);
                    kdtreejava.insert(keys[i], values[i], null);
                    sparseNdTree.insertWith(keys[i], values[i], null);
                }


                //System.out.println("ndtree: " + ndTree.size());
                //System.out.println("ndtree2: " + sparseNdTree.size());


//                printgraph(ndTree, "ndtree");
//                printgraph(sparseNdTree, "ndtree2");

                for (int j = 0; j < ins; j++) {
                    final double[] res = keys[j];
                    int finalJ = j;
                    ndTree.nearestN(res, 10, new Callback<Node[]>() {
                        @Override
                        public void on(Node[] result) {
                            sparseNdTree.nearestN(res, 10, new Callback<Node[]>() {
                                @Override
                                public void on(Node[] result2) {

                                    Assert.assertTrue(result2.length == result.length);
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


    //@Test
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

                ndTree.setAt(NDTree.BOUNDMIN, Type.DOUBLE_ARRAY, boundMin);
                ndTree.setAt(NDTree.BOUNDMAX, Type.DOUBLE_ARRAY, boundMax);
                ndTree.setAt(NDTree.PRECISION, Type.DOUBLE_ARRAY, precisions);


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

                    temp.set("key", Type.DOUBLE_ARRAY, key);
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
