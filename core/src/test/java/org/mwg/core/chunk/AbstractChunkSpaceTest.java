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
        ChunkSpace space = factory.newSpace(100, 100, null);

        StateChunk stateChunk = (StateChunk) space.create(ChunkType.STATE_CHUNK, 0, 0, 0, null, null);
        space.putAndMark(ChunkType.STATE_CHUNK, 0, 0, 0, stateChunk);

        WorldOrderChunk worldOrderChunk = (WorldOrderChunk) space.create(ChunkType.WORLD_ORDER_CHUNK, 0, 0, 1, null, null);
        space.putAndMark(ChunkType.WORLD_ORDER_CHUNK, 0, 0, 1, worldOrderChunk);

        TimeTreeChunk timeTreeChunk = (TimeTreeChunk) space.create(ChunkType.TIME_TREE_CHUNK, 0, 0, 2, null, null);
        space.putAndMark(ChunkType.TIME_TREE_CHUNK, 0, 0, 2, timeTreeChunk);

        GenChunk genChunk = (GenChunk) space.create(ChunkType.GEN_CHUNK, 1, 1, 1, null, null);
        space.putAndMark(ChunkType.GEN_CHUNK, 1, 1, 1, genChunk);

        space.freeAll();
    }

}
