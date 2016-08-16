package org.mwg.core.chunk;

import org.junit.Assert;
import org.junit.Test;
import org.mwg.Type;
import org.mwg.chunk.ChunkSpace;
import org.mwg.chunk.ChunkType;
import org.mwg.chunk.StateChunk;
import org.mwg.core.CoreConstants;
import org.mwg.plugin.MemoryFactory;
import org.mwg.struct.LongLongArrayMap;

public abstract class AbstractLongLongArrayMapTest {

    private MemoryFactory factory;

    public AbstractLongLongArrayMapTest(MemoryFactory factory) {
        this.factory = factory;
    }

    @Test
    public void genericTest() {

        ChunkSpace space = factory.newSpace(100, null);
        StateChunk chunk = (StateChunk) space.createAndMark(ChunkType.STATE_CHUNK, 0, 0, 0);
        LongLongArrayMap map = (LongLongArrayMap) chunk.getOrCreate(0, Type.LONG_TO_LONG_ARRAY_MAP);

        map.put(10, 10);
        Assert.assertTrue(map.size() == 1);
        Assert.assertTrue(map.get(10).length == 1);
        Assert.assertTrue(map.get(10)[0] == 10);

        map.put(10, 100);
        Assert.assertTrue(map.size() == 2);
        Assert.assertTrue(map.get(10).length == 2);
        Assert.assertTrue(map.get(10)[0] == 100);
        Assert.assertTrue(map.get(10)[1] == 10);


        map.put(10, 100);
        Assert.assertTrue(map.size() == 2);
        Assert.assertTrue(map.get(10).length == 2);


        //force reHash
        for (int i = 0; i < CoreConstants.MAP_INITIAL_CAPACITY; i++) {
            map.put(CoreConstants.BEGINNING_OF_TIME, i);
        }
        Assert.assertTrue(map.size() == CoreConstants.MAP_INITIAL_CAPACITY + 2);

        long[] getRes = map.get(CoreConstants.BEGINNING_OF_TIME);
        Assert.assertTrue(getRes.length == CoreConstants.MAP_INITIAL_CAPACITY);
        for (int i = 0; i < CoreConstants.MAP_INITIAL_CAPACITY; i++) {
            Assert.assertTrue(getRes[i] == (CoreConstants.MAP_INITIAL_CAPACITY - i - 1));
        }

        //test previous to reHash
        Assert.assertTrue(map.get(10).length == 2);
        Assert.assertTrue(map.get(10)[0] == 100);
        Assert.assertTrue(map.get(10)[1] == 10);

        //make a remove call
        map.remove(10, 10);
        Assert.assertTrue(map.size() == CoreConstants.MAP_INITIAL_CAPACITY + 2 - 1);
        Assert.assertTrue(map.get(10).length == 1);

        map.remove(CoreConstants.BEGINNING_OF_TIME, 0);
        Assert.assertTrue(map.size() == CoreConstants.MAP_INITIAL_CAPACITY + 2 - 2);
        getRes = map.get(CoreConstants.BEGINNING_OF_TIME);
        Assert.assertTrue(getRes.length == CoreConstants.MAP_INITIAL_CAPACITY - 1);
        for (int i = 1; i < CoreConstants.MAP_INITIAL_CAPACITY; i++) {
            Assert.assertTrue(getRes[i - 1] == (CoreConstants.MAP_INITIAL_CAPACITY - i));
        }

        space.free(chunk);
        space.freeAll();

    }

}
