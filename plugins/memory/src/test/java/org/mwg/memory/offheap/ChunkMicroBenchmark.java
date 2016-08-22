package org.mwg.memory.offheap;

import org.mwg.chunk.Chunk;
import org.mwg.chunk.ChunkType;

public class ChunkMicroBenchmark {

    public static void main(String[] args) {
        int nb = 3000000;
        long init = System.currentTimeMillis();
        OffHeapChunkSpace space = new OffHeapChunkSpace(nb, null);
        for (int i = 0; i < nb; i++) {
            space.createAndMark(ChunkType.TIME_TREE_CHUNK, 0, 0, i);

        }
        long begin = System.currentTimeMillis();
        for (int i = 0; i < nb; i++) {
            Chunk c = space.getAndMark(ChunkType.TIME_TREE_CHUNK, 0, 0, i);
            space.unmark(c.index());
        }
        long after = System.currentTimeMillis();
        System.out.println("total " + (after - init) + "ms, " + ((nb / 1000) / ((double) (after - init) / 1000d)) + " k chunk/s");
        System.out.println("insert " + (begin - init) + "ms, " + ((nb / 1000) / ((double) (begin - init) / 1000d)) + " k chunk/s");
        System.out.println("lookup " + (after - begin) + "ms, " + ((nb / 1000) / ((double) (after - begin) / 1000d)) + " k chunk/s");
    }

}
