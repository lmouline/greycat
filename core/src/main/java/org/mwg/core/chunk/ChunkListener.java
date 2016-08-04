package org.mwg.core.chunk;

import org.mwg.Graph;
import org.mwg.chunk.Chunk;

public interface ChunkListener {

    void declareDirty(Chunk chunk);

    Graph graph();

}
