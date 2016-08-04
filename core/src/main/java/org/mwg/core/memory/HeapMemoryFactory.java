package org.mwg.core.memory;

import org.mwg.Graph;
import org.mwg.chunk.ChunkSpace;
import org.mwg.core.chunk.heap.HeapChunkSpace;
import org.mwg.plugin.MemoryFactory;
import org.mwg.struct.Buffer;

public class HeapMemoryFactory implements MemoryFactory {

    @Override
    public ChunkSpace newSpace(long memorySize, long saveEvery, Graph graph) {
        return new HeapChunkSpace((int) memorySize, (int) saveEvery, graph);
    }

    @Override
    public Buffer newBuffer() {
        return new HeapBuffer();
    }
}
