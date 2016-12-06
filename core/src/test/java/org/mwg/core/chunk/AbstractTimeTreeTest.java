package org.mwg.core.chunk;

import org.junit.Assert;
import org.junit.Test;
import org.mwg.Constants;
import org.mwg.chunk.ChunkSpace;
import org.mwg.chunk.ChunkType;
import org.mwg.chunk.TimeTreeChunk;
import org.mwg.chunk.TreeWalker;
import org.mwg.plugin.MemoryFactory;
import org.mwg.struct.Buffer;

public abstract class AbstractTimeTreeTest {

    private MemoryFactory factory;

    public AbstractTimeTreeTest(MemoryFactory factory) {
        this.factory = factory;
    }

     @Test
    public void incrementalSave() {
        ChunkSpace space = factory.newSpace(100, null);
        TimeTreeChunk tree = (TimeTreeChunk) space.createAndMark(ChunkType.TIME_TREE_CHUNK, 0, 0, 0);
        Buffer buffer = factory.newBuffer();
        tree.saveDiff(buffer);
        Assert.assertTrue(compareWithString(buffer, ""));
        buffer.free();

        tree.insert(5);
        tree.insert(7);
        tree.insert(9);

        buffer = factory.newBuffer();
        tree.saveDiff(buffer);
        Assert.assertTrue(compareWithString(buffer, "G|K,O,S"));
        buffer.free();

        buffer = factory.newBuffer();
        tree.saveDiff(buffer);
        Assert.assertTrue(compareWithString(buffer, ""));
        buffer.free();

        space.free(tree);
        space.freeAll();

    }

    @Test
    public void nextTest() {
        ChunkSpace space = factory.newSpace(100, null);
        TimeTreeChunk tree = (TimeTreeChunk) space.createAndMark(ChunkType.TIME_TREE_CHUNK, 0, 0, 0);
        for (long i = 0; i <= 6; i++) {
            tree.insert(i);
        }
        tree.insert(8L);
        tree.insert(10L);
        tree.insert(11L);
        tree.insert(13L);

        Assert.assertEquals(tree.previous(-1), Constants.NULL_LONG);
        Assert.assertEquals(tree.previous(0), Constants.NULL_LONG);
        Assert.assertEquals(tree.previous(1), 0L);
        Assert.assertEquals(tree.previous(7), 6L);
        Assert.assertEquals(tree.previous(8), 6L);
        Assert.assertEquals(tree.previous(9), 8L);
        Assert.assertEquals(tree.previous(10), 8L);
        Assert.assertEquals(tree.previous(13), 11L);
        Assert.assertEquals(tree.previous(14), 13L);

        space.free(tree);
        space.freeAll();
    }

    @Test
    public void previousOrEqualsTest() {
        ChunkSpace space = factory.newSpace(100, null);
        TimeTreeChunk tree = (TimeTreeChunk) space.createAndMark(ChunkType.TIME_TREE_CHUNK, 0, 0, 0);
        for (long i = 0; i <= 6; i++) {
            tree.insert(i);
        }

        tree.insert(8L);
        tree.insert(10L);
        tree.insert(11L);
        tree.insert(13L);

        Assert.assertEquals(tree.previousOrEqual(-1), Constants.NULL_LONG);
        Assert.assertEquals(tree.previousOrEqual(0), 0L);
        Assert.assertEquals(tree.previousOrEqual(1), 1L);
        Assert.assertEquals(tree.previousOrEqual(7), 6L);
        Assert.assertEquals(tree.previousOrEqual(8), 8L);
        Assert.assertEquals(tree.previousOrEqual(9), 8L);
        Assert.assertEquals(tree.previousOrEqual(10), 10L);
        Assert.assertEquals(tree.previousOrEqual(13), 13L);
        Assert.assertEquals(tree.previousOrEqual(14), 13L);

        space.free(tree);
        space.freeAll();
    }

    @Test
    public void saveLoad() {

        ChunkSpace space = factory.newSpace(100, null);
        TimeTreeChunk tree = (TimeTreeChunk) space.createAndMark(ChunkType.TIME_TREE_CHUNK, 0, 0, 0);
        for (long i = 0; i <= 2; i++) {
            tree.insert(i);
        }
        Buffer buffer = factory.newBuffer();
        tree.save(buffer);
        Assert.assertTrue(compareWithString(buffer, "G|A,C,E"));
        Assert.assertTrue(tree.size() == 3);

        TimeTreeChunk tree2 = (TimeTreeChunk) space.createAndMark(ChunkType.TIME_TREE_CHUNK, 0, 0, 0);
        tree2.load(buffer);
        Assert.assertTrue(tree2.size() == 3);

        Buffer buffer2 = factory.newBuffer();
        tree2.save(buffer2);
        Assert.assertTrue(compareBuffers(buffer, buffer2));

        tree2.insert(10000);

        space.free(tree);
        space.free(tree2);

        buffer2.free();
        buffer.free();

        space.freeAll();

    }

    @Test
    public void massiveTest() {
        ChunkSpace space = factory.newSpace(100, null);
        TimeTreeChunk tree = (TimeTreeChunk) space.createAndMark(ChunkType.TIME_TREE_CHUNK, 0, 0, 0);
        long max = 100;
        for (long i = 0; i <= max; i = i + 2) {
            tree.insert(i);
        }
        for (long i = 1; i <= max; i = i + 2) {
            Assert.assertTrue(tree.previousOrEqual(i) == i - 1);
        }
        space.free(tree);
        space.freeAll();
    }

    @Test
    public void emptyHalfTest() {
        ChunkSpace space = factory.newSpace(100, null);
        TimeTreeChunk tree = (TimeTreeChunk) space.createAndMark(ChunkType.TIME_TREE_CHUNK, 0, 0, 0);
        int nbElements = 10;
        for (int i = 0; i < nbElements; i++) {
            tree.insert(i);
        }
        final long[] nbCall = {0};
        tree.range(org.mwg.Constants.BEGINNING_OF_TIME, org.mwg.Constants.END_OF_TIME, tree.size() / 2, new TreeWalker() {
            @Override
            public void elem(long t) {
                nbCall[0]++;
            }
        });
        Assert.assertTrue((nbElements / 2) == nbCall[0]);
        final long[] median = new long[1];
        nbCall[0] = 0;
        tree.range(org.mwg.Constants.BEGINNING_OF_TIME, org.mwg.Constants.END_OF_TIME, tree.size() / 2, new TreeWalker() {
            @Override
            public void elem(long t) {
                median[0] = 5;
                nbCall[0]++;
            }
        });
        Assert.assertTrue(median[0] == 5);
        Assert.assertTrue(nbCall[0] == 5);
        tree.clearAt(median[0]);
        nbCall[0] = 0;
        tree.range(org.mwg.Constants.BEGINNING_OF_TIME, org.mwg.Constants.END_OF_TIME, org.mwg.Constants.END_OF_TIME, new TreeWalker() {
            @Override
            public void elem(long t) {
                nbCall[0]++;
            }
        });
        Assert.assertTrue(nbCall[0] == 5);
        space.free(tree);
        space.freeAll();
    }

    private boolean compareWithString(Buffer buffer, String content) {
        for (int i = 0; i < content.length(); i++) {
            if (buffer.read(i) != content.codePointAt(i)) {
                return false;
            }
        }
        return true;
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


    /*
    @Test
    public void loadTest() {
        ChunkSpace space = factory.newSpace(100, null);
        TimeTreeChunk chunk = (TimeTreeChunk) space.createAndMark(ChunkType.TIME_TREE_CHUNK, 0, 0, 0);

        Buffer buffer = factory.newBuffer();
        buffer.writeAll("W|////////9,q5RzhfA,q5RzwIe,q5Rz+x+,q5R0Nba,q5R0cE4,q5R0quU,q5R05Xs,q5R1IBK,q5R1Wqo,q5R1lUM".getBytes());
        chunk.load(buffer);

        space.free(chunk);
        space.freeAll();
    }
*/

}
