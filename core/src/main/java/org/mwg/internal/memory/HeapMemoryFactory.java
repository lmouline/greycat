package org.mwg.internal.memory;

import org.mwg.Graph;
import org.mwg.chunk.ChunkSpace;
import org.mwg.internal.chunk.heap.HeapChunkSpace;
import org.mwg.plugin.MemoryFactory;
import org.mwg.struct.Buffer;

public class HeapMemoryFactory implements MemoryFactory {

    @Override
    public ChunkSpace newSpace(long memorySize, Graph graph, boolean deepWorld) {
        return new HeapChunkSpace((int) memorySize, graph, deepWorld);
    }

    @Override
    public Buffer newBuffer() {
        return new HeapBuffer();
    }
}
