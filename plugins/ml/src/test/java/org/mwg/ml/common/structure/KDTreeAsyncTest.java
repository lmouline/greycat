package org.mwg.ml.common.structure;

import org.junit.Assert;
import org.junit.Test;
import org.mwg.Callback;
import org.mwg.Graph;
import org.mwg.GraphBuilder;
import org.mwg.Node;
import org.mwg.ml.MLPlugin;
import org.mwg.ml.common.distance.EuclideanDistance;

import java.util.Random;

/**
 * @ignore ts
 */
public class KDTreeAsyncTest {
    @Test
    public void KDInsertTest() {
        final Graph graph = new GraphBuilder()
                .withPlugin(new MLPlugin())
                //.withScheduler(new NoopScheduler())
                .withMemorySize(100000)
                //.withOffHeapMemory()
                .build();
        graph.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean result) {
                KDTree testTask = (KDTree) graph.newTypedNode(0, 0, KDTree.NAME);
                testTask.set(KDTree.DISTANCE_THRESHOLD, 1e-30);

                KDNodeJava testjava = new KDNodeJava();
                testjava.setDistance(new EuclideanDistance());
                testjava.setThreshold(1e-30);


                final long initalcache = graph.space().available();


                int dim = 4;
                double[] vec = new double[dim];
                Random rand = new Random(125365L);
                int num = 100;
                graph.save(null);

                for (int i = 0; i < num; i++) {
                    double[] valuecop = new double[vec.length];
                    for (int j = 0; j < dim; j++) {
                        vec[j] = rand.nextDouble();
                        vec[j] = rand.nextDouble();
                        valuecop[j] = vec[j];
                    }

                    Node value = graph.newNode(0, 0);
                    value.set("value", valuecop);

                    testTask.insert(vec, value, null);
                    testjava.insert(vec, value, null);
                    value.free();
                }


                graph.save(null);
                long finalcache = graph.space().available();


                double[] key = new double[dim];
                for (int i = 0; i < dim; i++) {
                    key[i] = 0.1 * (i + 1);
                }

                testjava.nearestN(key, 8, new Callback<Object[]>() {
                    @Override
                    public void on(Object[] result) {

                        testTask.nearestN(key, 8, new Callback<Node[]>() {
                            @Override
                            public void on(Node[] result3) {
                                for (int i = 0; i < result3.length; i++) {
                                    Assert.assertTrue(((Node) result[i]).id() == result3[i].id());
                                    result3[i].free();
                                }
                                graph.save(null);
                                long finalcache = graph.space().available();
                               // System.out.println("init " + initalcache + " end cache: " + finalcache);
                                Assert.assertTrue(finalcache == initalcache);
                                //System.out.println();
                            }
                        });
                    }
                });


            }
        });
    }
}
