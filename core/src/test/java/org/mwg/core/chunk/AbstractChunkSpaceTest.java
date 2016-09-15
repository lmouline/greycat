package org.mwg.core.chunk;

import org.junit.Test;
import org.mwg.chunk.*;
import org.mwg.plugin.MemoryFactory;

public abstract class AbstractChunkSpaceTest {

    private MemoryFactory factory;

    public AbstractChunkSpaceTest(MemoryFactory factory) {
        this.factory = factory;
    }

    @Test
    public void globalTest() {
        
        ChunkSpace space = factory.newSpace(100, null);

        StateChunk stateChunk = (StateChunk) space.createAndMark(ChunkType.STATE_CHUNK, 0, 0, 0);

        WorldOrderChunk worldOrderChunk = (WorldOrderChunk) space.createAndMark(ChunkType.WORLD_ORDER_CHUNK, 0, 0, 1);

        TimeTreeChunk timeTreeChunk = (TimeTreeChunk) space.createAndMark(ChunkType.TIME_TREE_CHUNK, 0, 0, 2);

        GenChunk genChunk = (GenChunk) space.createAndMark(ChunkType.GEN_CHUNK, 1, 1, 1);

        space.free(stateChunk);
        space.free(worldOrderChunk);
        space.free(timeTreeChunk);
        space.free(genChunk);

        space.freeAll();

    }

}
