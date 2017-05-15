package greycatTest.internal;

import greycat.Callback;
import greycat.Graph;
import greycat.GraphBuilder;
import greycat.Node;
import greycat.scheduler.NoopScheduler;
import org.junit.Assert;
import org.junit.Test;

public class NewResolverTest {

    @Test
    public void test() {
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

}
