package org.mwg.memory.offheap;

import org.mwg.Graph;
import org.mwg.chunk.ChunkSpace;
import org.mwg.plugin.MemoryFactory;
import org.mwg.struct.Buffer;

public class OffHeapMemoryFactory implements MemoryFactory {

    @Override
    public final ChunkSpace newSpace(final long memorySize, final Graph graph, boolean deepWorld) {
        return new OffHeapChunkSpace(memorySize, graph);
    }

    @Override
    public final Buffer newBuffer() {
        return new OffHeapBuffer();
    }

}
