package greycatTest.internal;

import greycat.Graph;
import greycat.GraphBuilder;
import greycat.Node;
import greycat.Type;
import greycat.base.BaseNode;
import greycat.scheduler.NoopScheduler;
import org.junit.Assert;
import org.junit.Test;

public class HiddenClone {

    @Test
    public void test(){
        Graph g = GraphBuilder.newBuilder().withScheduler(new NoopScheduler()).build();
        g.connect(null);
        Node n = g.newNode(0,0);
        n.set("name", Type.STRING,"hello");
        Node clone = ((BaseNode)n).createClone();
        Assert.assertEquals(clone.get("name"),n.get("name"));
    }

}
