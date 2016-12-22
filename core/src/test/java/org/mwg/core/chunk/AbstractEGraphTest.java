package org.mwg.core.chunk;

import org.junit.Assert;
import org.junit.Test;
import org.mwg.Graph;
import org.mwg.GraphBuilder;
import org.mwg.Type;
import org.mwg.chunk.ChunkSpace;
import org.mwg.chunk.ChunkType;
import org.mwg.chunk.StateChunk;
import org.mwg.core.scheduler.NoopScheduler;
import org.mwg.plugin.MemoryFactory;
import org.mwg.struct.EGraph;
import org.mwg.struct.ENode;
import org.mwg.struct.ERelation;
import org.mwg.struct.LMatrix;

public abstract class AbstractEGraphTest {

    private MemoryFactory factory;

    public AbstractEGraphTest(MemoryFactory factory) {
        this.factory = factory;
    }

    @Test
    public void simpleUsageTest() {
        Graph g = GraphBuilder.newBuilder().withScheduler(new NoopScheduler()).build();
        g.connect(null);

        ChunkSpace space = factory.newSpace(100, g);
        StateChunk chunk = (StateChunk) space.createAndMark(ChunkType.STATE_CHUNK, 0, 0, 0);
        //test embedded graph attribute
        EGraph egraph = (EGraph) chunk.getOrCreate(0, Type.EGRAPH);

        //test primitive attribute
        ENode eNode = egraph.newNode();
        egraph.setRoot(eNode);
        eNode.set("name", Type.STRING, "hello");
        Assert.assertEquals("{\"name\":\"hello\"}", eNode.toString());

        //test single eRelation
        ENode secondENode = egraph.newNode();
        secondENode.set("name", Type.STRING, "secondNode");
        eNode.set("children", Type.ENODE, secondENode);
        ENode retrieved = (ENode) eNode.get("children");
        Assert.assertEquals(retrieved.toString(), retrieved.toString());

        //test eRelation
        ERelation eRelation = (ERelation) eNode.getOrCreate("testRel", Type.ERELATION);
        for (int i = 0; i < 3; i++) {
            ENode loopNode = egraph.newNode();
            secondENode.set("name", Type.STRING, "node_" + i);
            eRelation.add(loopNode);
        }

        ERelation resolvedERelation = (ERelation) eNode.get("testRel");
        Assert.assertEquals(3, resolvedERelation.size());
        Assert.assertEquals("[2,3,4]", resolvedERelation.toString());

    }

    @Test
    public void volatildeTest() {
        Graph g = GraphBuilder.newBuilder().withScheduler(new NoopScheduler()).build();
        g.connect(null);

        ChunkSpace space = factory.newSpace(100, g);

        EGraph eGraph = space.newVolatileGraph();
        ENode eNode = eGraph.newNode();

        Assert.assertNotNull(eGraph);
        Assert.assertNotNull(eNode);

        LMatrix lmat = (LMatrix) eNode.getOrCreate("lmat", Type.LMATRIX);
        Assert.assertNotNull(lmat);

        lmat.appendColumn(new long[]{1L, 2L, 3L});

    }


}
