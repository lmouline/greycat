package org.mwg.core.chunk;

import org.junit.Assert;
import org.junit.Test;
import org.mwg.chunk.ChunkSpace;
import org.mwg.chunk.ChunkType;
import org.mwg.chunk.GenChunk;
import org.mwg.plugin.MemoryFactory;
import org.mwg.struct.Buffer;
import org.mwg.utility.Base64;

public abstract class AbstractGenChunkTest {

    private MemoryFactory factory;

    public AbstractGenChunkTest(MemoryFactory factory) {
        this.factory = factory;
    }

    @Test
    public void genTest() {

        ChunkSpace space = factory.newSpace(100, null);
        GenChunk genChunk = (GenChunk) space.createAndMark(ChunkType.GEN_CHUNK, 0, 0, 0);

        Assert.assertEquals(genChunk.newKey(), 1);
        Assert.assertEquals(genChunk.newKey(), 2);
        Assert.assertEquals(genChunk.newKey(), 3);

        space.free(genChunk);

        Buffer buf = factory.newBuffer();
        Base64.encodeLongToBuffer(100, buf);
        GenChunk genChunk2 = (GenChunk) space.createAndMark(ChunkType.GEN_CHUNK, 0, 0, 0);
        genChunk2.load(buf, true);
        buf.free();

        Assert.assertEquals(genChunk2.newKey(), 101);
        Assert.assertEquals(genChunk2.newKey(), 102);
        Assert.assertEquals(genChunk2.newKey(), 103);

        Buffer bufSave = factory.newBuffer();
        genChunk2.save(bufSave);
        byte[] flatSaved = bufSave.data();
        Assert.assertEquals(flatSaved.length, 2);
        Assert.assertEquals(flatSaved[0], 68);
        Assert.assertEquals(flatSaved[1], 79);
        Assert.assertEquals(103, Base64.decodeToLongWithBounds(bufSave, 0, 2));

        bufSave.free();
        space.free(genChunk2);

        GenChunk genChunk_100 = (GenChunk) space.createAndMark(ChunkType.GEN_CHUNK, 0, 0, 100);
        Assert.assertEquals(genChunk_100.newKey(), 13743895347201L);
        Assert.assertEquals(genChunk_100.newKey(), 13743895347202L);

        space.free(genChunk_100);
        space.freeAll();

    }


}
