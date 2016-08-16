package org.mwg.memory.offheap;

import org.mwg.Graph;
import org.mwg.chunk.ChunkSpace;
import org.mwg.plugin.MemoryFactory;
import org.mwg.struct.Buffer;

public class OffHeapMemoryFactory implements MemoryFactory {

    @Override
    public ChunkSpace newSpace(long memorySize, Graph graph) {
        return null;
        //return new OffHeapChunkSpace(memorySize, graph);
    }

    @Override
    public Buffer newBuffer() {
        return new OffHeapBuffer();
    }

}
