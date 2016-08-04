package org.mwg.memory.offheap;

import org.mwg.Graph;
import org.mwg.chunk.ChunkSpace;
import org.mwg.plugin.MemoryFactory;
import org.mwg.struct.Buffer;

public class OffHeapMemoryFactory implements MemoryFactory {
    @Override
    public ChunkSpace newSpace(long memorySize, long saveEvery, Graph graph) {
        return new OffHeapChunkSpace(memorySize, saveEvery, graph);
    }

    @Override
    public Buffer newBuffer() {
        return new OffHeapBuffer();
    }
}
