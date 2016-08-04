package org.mwg.ml.common.structure;

import org.junit.Assert;
import org.junit.Test;
import org.mwg.Callback;
import org.mwg.Graph;
import org.mwg.GraphBuilder;
import org.mwg.Node;
import org.mwg.core.scheduler.NoopScheduler;
import org.mwg.ml.MLPlugin;
import org.mwg.ml.MLTestPlugin;

import java.util.Random;
import java.util.Scanner;

/**
 * @ignore ts
 */
public class KDTreeAsyncTest {
    //@Test
    public void KDInsertTest() {
        final Graph graph = new GraphBuilder()
                .withPlugin(new MLTestPlugin())
                .withPlugin(new MLPlugin())
                .withScheduler(new NoopScheduler())
                .withMemorySize(100000)
                //.withOffHeapMemory()
                .build();
        graph.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean result) {
                KDTreeAsync test = (KDTreeAsync) graph.newTypedNode(0, 0, KDTreeAsync.NAME);
                test.set(KDTreeAsync.DISTANCE_THRESHOLD, 1e-30);

                KDTree testTask = (KDTree) graph.newTypedNode(0, 0, KDTree.NAME);
                testTask.set(KDTree.DISTANCE_THRESHOLD, 1e-30);

                final long initalcache = graph.space().available();


                int dim = 4;
                double[] vec = new double[dim];
                Random rand = new Random(125365L);
                int num = 137;
                graph.save(null);

                for (int i = 0; i < num; i++) {
                    double[] valuecop = new double[vec.length];
                    for (int j = 0; j < dim; j++) {
                        vec[j] = rand.nextDouble();
                        valuecop[j] = vec[j];
                    }

                    Node value = graph.newNode(0, 0);
                    value.set("value", valuecop);

                    test.insert(vec, value, null);
                    testTask.insert(vec, value, null);
                    value.free();
                }







                graph.save(null);
                Assert.assertTrue((int) test.get(KDTreeAsync.NUM_NODES) == num);
                graph.save(null);
                long finalcache = graph.space().available();


                double[] key = new double[dim];
                for (int i = 0; i < dim; i++) {
                    key[i] = 0.1 * (i + 1);
                }

                test.nearestN(key, 8, new Callback<Node[]>() {
                            @Override
                            public void on(Node[] result1) {
                                Assert.assertTrue(result1.length == 8);
                                testTask.nearestN(key, 8, new Callback<Node[]>() {
                                    @Override
                                    public void on(Node[] result) {
                                        Assert.assertTrue(result.length == 8);
                                        for(int i=0;i<result.length;i++){
                                            Assert.assertTrue(result[i].id()==result1[i].id());
                                            result1[i].free();
                                            result[i].free();
                                        }
                                        graph.save(null);
                                        long finalcache = graph.space().available();
                                        System.out.println("init "+initalcache+" end cache: "+finalcache);
                                        Assert.assertTrue(finalcache==initalcache);
                                    }
                                });
                            }
                        });





           /*     test.nearestN(key, 8, new Callback<Node[]>() {
                    @Override
                    public void on(Node[] result1) {
                        Assert.assertTrue(result1.length == 8);

                      /*  for(int i=0;i<result1.length;i++){
                            result1[i].free();
                        }*/

                     /*   long finalcache = graph.space().available();
                        System.out.println("init "+initalcache+" "+finalcache);
                        Assert.assertTrue(finalcache==initalcache);

                       testTask.nearestN(key, 8, new Callback<Node[]>() {
                            @Override
                            public void on(Node[] result) {
                                Assert.assertTrue(result.length == 8);
                                for(int i=0;i<result.length;i++){
                                    Assert.assertTrue(result[i].id()==result1[i].id());
                                    result[i].free();
                                    result1[i].free();
                                }
                                graph.save(null);
                                long finalcache = graph.space().available();
                                System.out.println("init "+initalcache+" "+finalcache);
                                Assert.assertTrue(finalcache==initalcache);
                            }
                        });*/



               //     }
              //  });

            }
        });
    }
}
