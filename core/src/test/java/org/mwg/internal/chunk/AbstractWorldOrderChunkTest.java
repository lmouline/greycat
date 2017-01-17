package org.mwg.internal.chunk;

import org.junit.Assert;
import org.junit.Test;
import org.mwg.chunk.ChunkSpace;
import org.mwg.chunk.ChunkType;
import org.mwg.chunk.WorldOrderChunk;
import org.mwg.internal.CoreConstants;
import org.mwg.plugin.MemoryFactory;
import org.mwg.struct.Buffer;

public abstract class AbstractWorldOrderChunkTest {

    private MemoryFactory factory;

    public AbstractWorldOrderChunkTest(MemoryFactory factory) {
        this.factory = factory;
    }

    //@Test
    public void incrementalSave() {
        ChunkSpace space = factory.newSpace(100, null);
        WorldOrderChunk map = (WorldOrderChunk) space.createAndMark(ChunkType.WORLD_ORDER_CHUNK, 0, 0, 1);

        Buffer buffer = factory.newBuffer();
        map.saveDiff(buffer);
        Assert.assertTrue(compareWithString(buffer, ""));
        buffer.free();

        map.put(5, 6);
        map.put(7, 8);
        map.put(9, 10);

        buffer = factory.newBuffer();
        map.saveDiff(buffer);
        Assert.assertTrue(compareWithString(buffer, "G|K:M,O:Q,S:U"));
        buffer.free();

        buffer = factory.newBuffer();
        map.saveDiff(buffer);
        Assert.assertTrue(compareWithString(buffer, ""));
        buffer.free();

        space.free(map);
        space.freeAll();

    }


    @Test
    public void simpleTest() {
        ChunkSpace space = factory.newSpace(100, null);
        WorldOrderChunk map = (WorldOrderChunk) space.createAndMark(ChunkType.WORLD_ORDER_CHUNK, 0, 0, 0);
        //mass insert
        for (long i = 0; i < 10; i++) {
            map.put(i, i * 3);
        }
        //mass check
        for (long i = 0; i < 10; i++) {
            Assert.assertTrue(map.get(i) == i * 3);
        }
        space.free(map);
        space.freeAll();
    }

    @Test
    public void orderTest() {
        ChunkSpace space = factory.newSpace(100, null);
        WorldOrderChunk map = (WorldOrderChunk) space.createAndMark(ChunkType.WORLD_ORDER_CHUNK, 0, 0, 0);
        //mass insert
        for (long i = 0; i < 10000; i++) {
            map.put(i, i * 3);
        }
        //mass check
        for (long i = 0; i < 10000; i++) {
            Assert.assertTrue(map.get(i) == i * 3);
        }
        space.free(map);
        space.freeAll();
    }

    @Test
    public void saveLoadTest() {
        ChunkSpace space = factory.newSpace(100, null);
        WorldOrderChunk map = (WorldOrderChunk) space.createAndMark(ChunkType.WORLD_ORDER_CHUNK, 0, 0, 1);
        //mass insert
        for (long i = 0; i < 10000; i++) {
            map.put(i, i * 3);
        }
        Assert.assertTrue(map.extra() == CoreConstants.NULL_LONG);
        map.setExtra(1000000);
        Assert.assertTrue(map.size() == 10000);
        Assert.assertTrue(map.extra() == 1000000);
        Buffer buffer = factory.newBuffer();
        map.save(buffer);
        WorldOrderChunk map2 = (WorldOrderChunk) space.createAndMark(ChunkType.WORLD_ORDER_CHUNK, 0, 0, 2);
        map2.load(buffer);
        for (long i = 0; i < 10000; i++) {
            Assert.assertTrue(map2.get(i) == i * 3);
        }
        Assert.assertTrue(map2.extra() == 1000000);
        Buffer buffer2 = factory.newBuffer();
        map2.save(buffer2);
        Assert.assertTrue(compareBuffers(buffer, buffer2));
        buffer.free();
        buffer2.free();
        space.free(map);
        space.free(map2);
        space.freeAll();
    }

    private boolean compareBuffers(Buffer buffer, Buffer buffer2) {
        if (buffer.length() != buffer2.length()) {
            return false;
        }
        for (int i = 0; i < buffer.length(); i++) {
            if (buffer.read(i) != buffer2.read(i)) {
                return false;
            }
        }
        return true;
    }

    private boolean compareWithString(Buffer buffer, String content) {
        for (int i = 0; i < content.length(); i++) {
            if (buffer.read(i) != content.codePointAt(i)) {
                return false;
            }
        }
        return true;
    }

}
