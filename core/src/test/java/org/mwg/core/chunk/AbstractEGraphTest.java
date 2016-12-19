package org.mwg.core.chunk;

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

        EGraph egraph = (EGraph) chunk.getOrCreate(0, Type.EGRAPH);

        ENode eNode = egraph.newNode();
        egraph.setRoot(eNode);
        eNode.set("name",Type.STRING,"hello");
        System.out.println(eNode.toString());


        //System.out.println(egraph);
        //TODO
    }

    //TODO

}
