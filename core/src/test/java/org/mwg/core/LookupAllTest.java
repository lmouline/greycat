package org.mwg.core;

import org.junit.Assert;
import org.junit.Test;
import org.mwg.Callback;
import org.mwg.Graph;
import org.mwg.GraphBuilder;
import org.mwg.Node;

public class LookupAllTest {

    @Test
    public void test() {
        final Graph graph = new GraphBuilder().build();
        graph.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean o) {
                final long availableAfterConnect = graph.space().available();
                final org.mwg.Node node0 = graph.newNode(0, 0);
                final long id = node0.id();

                final org.mwg.Node node2 = graph.newNode(0, 0);
                final long id2 = node2.id();

                node0.free();
                node2.free();

                graph.save(new Callback<Boolean>() {
                    @Override
                    public void on(Boolean result) {
                        //do something selectWith the node
                        graph.lookup(0, 0, id, new Callback<org.mwg.Node>() {
                            @Override
                            public void on(org.mwg.Node result) {
                                //check that the lookup return the same
                                Assert.assertTrue(result.id() == id);
                                result.free();

                                graph.resolver().lookupAll(0, 0, new long[]{id, id2}, new Callback<Node[]>() {
                                    @Override
                                    public void on(Node[] result) {
                                        Assert.assertTrue(result[0].id() == id);
                                        Assert.assertTrue(result[1].id() == id2);

                                        graph.freeNodes(result);

                                        final long availableAfter = graph.space().available();
                                        Assert.assertEquals(availableAfterConnect, availableAfter);

                                        graph.disconnect(null);

                                    }
                                });

                            }
                        });
                    }
                });
            }
        });
    }
}
