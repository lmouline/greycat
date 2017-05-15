package greycatTest.internal;

import greycat.*;
import greycat.base.BaseNode;
import greycat.chunk.SuperTimeTreeChunk;
import greycat.chunk.TimeTreeChunk;
import greycat.internal.CoreConstants;
import greycat.scheduler.NoopScheduler;
import org.junit.Assert;
import org.junit.Test;

public class ResolverTest {

    @Test
    public void endTest() {
        Graph g = GraphBuilder.newBuilder().withScheduler(new NoopScheduler()).build();
        g.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean connectionResult) {
                Node n = g.newNode(0, 0);
                Assert.assertEquals("{\"world\":0,\"time\":0,\"id\":1}", n.toString());

                g.lookup(0, 10, n.id(), new Callback<Node>() {
                    @Override
                    public void on(final Node unPhased) {
                        Assert.assertEquals("{\"world\":0,\"time\":10,\"id\":1}", unPhased.toString());
                        unPhased.end();
                        g.lookup(0, 15, n.id(), new Callback<Node>() {
                            @Override
                            public void on(final Node dead) {
                                Assert.assertTrue(dead == null);
                            }
                        });
                    }
                });

            }
        });
    }

    @Test
    public void timeTest() {
        Graph g = GraphBuilder.newBuilder().withScheduler(new NoopScheduler()).build();
        g.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean connectionResult) {
                BaseNode n = (BaseNode) g.newNode(0, 0);
                for (long i = 0; i < CoreConstants.TREE_SCALES[0]; i++) {
                    final long finalI = i;
                    g.lookup(0, i, n.id(), new Callback<Node>() {
                        @Override
                        public void on(Node result) {
                            result.set("time", Type.LONG, finalI);
                        }
                    });
                }
                SuperTimeTreeChunk stt = (SuperTimeTreeChunk) g.space().get(n._index_superTimeTree);
                TimeTreeChunk tt = (TimeTreeChunk) g.space().get(n._index_timeTree);
                Assert.assertTrue(stt.size() == 1);
                Assert.assertTrue(tt.size() == CoreConstants.TREE_SCALES[0]);
                for (long i = CoreConstants.TREE_SCALES[0]; i < CoreConstants.TREE_SCALES[1]; i++) {
                    final long finalI = i;
                    g.lookup(0, i, n.id(), new Callback<Node>() {
                        @Override
                        public void on(Node result) {
                            result.set("time", Type.LONG, finalI);
                        }
                    });
                }
                stt = (SuperTimeTreeChunk) g.space().get(n._index_superTimeTree);
                Assert.assertTrue(stt.size() == 10);
                System.out.println(n);
            }
        });
    }

    @Test
    public void pastInsertTest() {
        Graph g = GraphBuilder.newBuilder().withScheduler(new NoopScheduler()).build();
        g.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean connectionResult) {
                BaseNode n = (BaseNode) g.newNode(0, 0);

                for (long i = 0; i < CoreConstants.TREE_SCALES[0]; i++) {
                    final long finalI = i;
                    g.lookup(0, i, n.id(), new Callback<Node>() {
                        @Override
                        public void on(Node result) {
                            result.set("time", Type.LONG, finalI);
                        }
                    });
                }
                g.lookup(0, CoreConstants.TREE_SCALES[0] * 2, n.id(), new Callback<Node>() {
                    @Override
                    public void on(Node result) {
                        result.set("time", Type.LONG, CoreConstants.TREE_SCALES[0]);
                    }
                });
                g.lookup(0, CoreConstants.TREE_SCALES[0] + 100, n.id(), new Callback<Node>() {
                    @Override
                    public void on(Node result) {
                        result.set("time", Type.LONG, CoreConstants.TREE_SCALES[0] + 100);
                    }
                });
                SuperTimeTreeChunk stt = (SuperTimeTreeChunk) g.space().get(n._index_superTimeTree);
                Assert.assertTrue(stt.size() == 2);
            }
        });
    }

}
