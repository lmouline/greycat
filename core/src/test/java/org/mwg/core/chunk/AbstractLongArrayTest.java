package org.mwg.core.chunk;

import org.junit.Assert;
import org.junit.Test;
import org.mwg.Type;
import org.mwg.chunk.ChunkSpace;
import org.mwg.chunk.ChunkType;
import org.mwg.chunk.StateChunk;
import org.mwg.plugin.MemoryFactory;
import org.mwg.struct.LongArray;

public abstract class AbstractLongArrayTest {

    private MemoryFactory factory;

    public AbstractLongArrayTest(MemoryFactory factory) {
        this.factory = factory;
    }

    @Test
    public void genericTest() {

        ChunkSpace space = factory.newSpace(100, 100, null);
        StateChunk chunk = (StateChunk) space.createAndMark(ChunkType.STATE_CHUNK, 0, 0, 0);
        LongArray longArray = (LongArray) chunk.getOrCreate(0, Type.RELATION);

        Assert.assertEquals(longArray.size(), 0);
        longArray.add(-1);
        longArray.add(10);
        longArray.add(100);
        Assert.assertEquals(longArray.size(), 3);

        Assert.assertEquals(longArray.get(0), -1);
        Assert.assertEquals(longArray.get(1), 10);
        Assert.assertEquals(longArray.get(2), 100);

        for (int i = 200; i < 1000; i++) {
            longArray.add(i);
        }

        Assert.assertEquals(longArray.size(), 803);
        Assert.assertEquals(longArray.get(802), 999);

        longArray.remove(3000);
        Assert.assertEquals(longArray.size(), 803);
        longArray.remove(950);
        Assert.assertEquals(longArray.size(), 802);

        space.free(chunk);
        space.freeAll();

    }

}
