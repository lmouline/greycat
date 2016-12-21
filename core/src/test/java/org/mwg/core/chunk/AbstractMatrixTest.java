package org.mwg.core.chunk;

import org.junit.Assert;
import org.junit.Test;
import org.mwg.Type;
import org.mwg.chunk.ChunkSpace;
import org.mwg.chunk.ChunkType;
import org.mwg.chunk.StateChunk;
import org.mwg.plugin.MemoryFactory;
import org.mwg.struct.Buffer;
import org.mwg.struct.Matrix;

public abstract class AbstractMatrixTest {

    private MemoryFactory factory;

    public AbstractMatrixTest(MemoryFactory factory) {
        this.factory = factory;
    }

    @Test
    public void genericTest() {

        ChunkSpace space = factory.newSpace(100, null);

        StateChunk chunk = (StateChunk) space.createAndMark(ChunkType.STATE_CHUNK, 0, 0, 0);
        Matrix matrix = (Matrix) chunk.getOrCreate(0, Type.MATRIX);

        matrix.init(3, 4); //3 rows, 4 columns

        Assert.assertEquals(3, matrix.rows());
        Assert.assertEquals(4, matrix.columns());

        for (double i = 0; i < matrix.rows(); i++) {
            for (double j = 0; j < matrix.columns(); j++) {
                matrix.set((int) i, (int) j, (i + j) / 100);
            }
        }

        for (double i = 0; i < matrix.rows(); i++) {
            for (double j = 0; j < matrix.columns(); j++) {
                Assert.assertEquals(matrix.get((int) i, (int) j) + "", "" + ((i + j) / 100));
            }
        }

        Buffer buffer = factory.newBuffer();
        chunk.save(buffer);

        //  Assert.assertEquals(buffer.toString(), "C|A,a,c:QAI:QBA:AAA:P4EeuFHrhR7:P5EeuFHrhR7:P4EeuFHrhR7:P5EeuFHrhR7:P5OuFHrhR64:P5EeuFHrhR7:P5OuFHrhR64:P6EeuFHrhR7:P5OuFHrhR64:P6EeuFHrhR7:P6JmZmZmZma");

        StateChunk chunk2 = (StateChunk) space.createAndMark(ChunkType.STATE_CHUNK, 0, 0, 1);
        chunk2.load(buffer);

        Buffer bufferAfter = factory.newBuffer();
        chunk2.save(bufferAfter);

        // Assert.assertEquals(bufferAfter.toString(), "C|A,a,c:QAI:QBA:AAA:P4EeuFHrhR7:P5EeuFHrhR7:P4EeuFHrhR7:P5EeuFHrhR7:P5OuFHrhR64:P5EeuFHrhR7:P5OuFHrhR64:P6EeuFHrhR7:P5OuFHrhR64:P6EeuFHrhR7:P6JmZmZmZma");

        Assert.assertTrue(compareBuffers(buffer, bufferAfter));


        buffer.free();
        bufferAfter.free();

        space.free(chunk);
        space.free(chunk2);
        space.freeAll();

    }

    @Test
    public void extractTest() {
        ChunkSpace space = factory.newSpace(100, null);
        StateChunk chunk = (StateChunk) space.createAndMark(ChunkType.STATE_CHUNK, 0, 0, 0);
        Matrix matrix = (Matrix) chunk.getOrCreate(0, Type.MATRIX);
        matrix.init(3, 2);
        for (int i = 0; i < 2; i++) {
            matrix.set(0, i, i + 1);
            matrix.set(1, i, i + 1);
            matrix.set(2, i, i + 1);
        }
        for (int i = 0; i < 2; i++) {
            double[] extracted = matrix.column(i);
            Assert.assertEquals(3, extracted.length);
            double val = i + 1;
            Assert.assertEquals(val + "", extracted[0] + "");
            Assert.assertEquals(val + "", extracted[1] + "");
            Assert.assertEquals(val + "", extracted[2] + "");
        }
        space.free(chunk);
        space.freeAll();
    }

    @Test
    public void appendTest() {
        ChunkSpace space = factory.newSpace(100, null);
        StateChunk chunk = (StateChunk) space.createAndMark(ChunkType.STATE_CHUNK, 0, 0, 0);
        Matrix matrix = (Matrix) chunk.getOrCreate(0, Type.MATRIX);
        matrix.appendColumn(new double[]{1, 2, 3});
        matrix.appendColumn(new double[]{4, 5, 6});

        Assert.assertEquals(matrix.rows(), 3);
        Assert.assertEquals(matrix.columns(), 2);

        Assert.assertTrue(matrix.get(0, 0) == 1);
        Assert.assertTrue(matrix.get(1, 0) == 2);
        Assert.assertTrue(matrix.get(2, 0) == 3);

        Assert.assertTrue(matrix.get(0, 1) == 4);
        Assert.assertTrue(matrix.get(1, 1) == 5);
        Assert.assertTrue(matrix.get(2, 1) == 6);

        space.free(chunk);
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


}
