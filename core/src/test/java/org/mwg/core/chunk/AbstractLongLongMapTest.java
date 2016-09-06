package org.mwg.core.chunk;

import org.junit.Assert;
import org.junit.Test;
import org.mwg.Type;
import org.mwg.chunk.ChunkSpace;
import org.mwg.chunk.ChunkType;
import org.mwg.chunk.StateChunk;
import org.mwg.core.CoreConstants;
import org.mwg.plugin.MemoryFactory;
import org.mwg.struct.LongLongMap;
import org.mwg.struct.LongLongMapCallBack;

public abstract class AbstractLongLongMapTest {

    private MemoryFactory factory;

    public AbstractLongLongMapTest(MemoryFactory factory) {
        this.factory = factory;
    }

    @Test
    public void genericTest() {

        ChunkSpace space = factory.newSpace(100, null);
        StateChunk chunk = (StateChunk) space.createAndMark(ChunkType.STATE_CHUNK, 0, 0, 0);
        LongLongMap map = (LongLongMap) chunk.getOrCreate(0, Type.LONG_TO_LONG_MAP);

        Assert.assertEquals(map.size(), 0);

        map.put(0, 0);
        Assert.assertEquals(map.size(), 1);
        Assert.assertTrue(0 == map.get(0));

        map.put(1, 1);
        Assert.assertEquals(map.size(), 2);
        Assert.assertTrue(0 == map.get(0));
        Assert.assertTrue(1 == map.get(1));

        //no effect
        map.put(1, 1);

        map.put(0, 1);
        map.put(1, 2);
        Assert.assertTrue(1 == map.get(0));
        Assert.assertTrue(2 == map.get(1));

        map.put(2, 2);
        Assert.assertTrue(2 == map.get(2));

        final long[] keys = new long[3];
        final long[] values = new long[3];
        final int[] resIndex = {0};
        map.each(new LongLongMapCallBack() {
            @Override
            public void on(long key, long value) {
                keys[resIndex[0]] = key;
                values[resIndex[0]] = value;
                resIndex[0]++;
            }
        });
        Assert.assertTrue(1 == values[0]);
        Assert.assertTrue(2 == values[1]);
        Assert.assertTrue(2 == values[2]);
        Assert.assertTrue(0 == keys[0]);
        Assert.assertTrue(1 == keys[1]);
        Assert.assertTrue(2 == keys[2]);

        //force the graph to do a rehash capacity
        for (int i = 0; i < CoreConstants.MAP_INITIAL_CAPACITY; i++) {
            map.put(i, i);
        }
        //test that all values are consistent
        for (int i = 0; i < CoreConstants.MAP_INITIAL_CAPACITY; i++) {
            Assert.assertTrue(map.get(i) == i);
        }
        space.free(chunk);
        space.freeAll();

    }

}
