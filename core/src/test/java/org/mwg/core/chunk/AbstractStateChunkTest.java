package org.mwg.core.chunk;

import org.junit.Assert;
import org.junit.Test;
import org.mwg.Constants;
import org.mwg.Type;
import org.mwg.chunk.ChunkSpace;
import org.mwg.chunk.ChunkType;
import org.mwg.chunk.StateChunk;
import org.mwg.utility.HashHelper;
import org.mwg.plugin.MemoryFactory;
import org.mwg.struct.Buffer;
import org.mwg.struct.LongLongArrayMap;
import org.mwg.struct.LongLongMap;
import org.mwg.struct.StringLongMap;

public abstract class AbstractStateChunkTest {

    private MemoryFactory factory;

    public AbstractStateChunkTest(MemoryFactory factory) {
        this.factory = factory;
    }

    @Test
    public void saveLoadTest() {

        ChunkSpace space = factory.newSpace(100, null);
        StateChunk chunk = (StateChunk) space.createAndMark(ChunkType.STATE_CHUNK, 0, 0, 0);

        //init chunk selectWith primitives
        chunk.set(0, Type.BOOL, true);
        Assert.assertEquals(chunk.get(0), true);

        chunk.set(1, Type.STRING, "hello");
        Assert.assertEquals(chunk.get(1), "hello");

        chunk.set(2, Type.DOUBLE, 1.0);
        Assert.assertEquals(chunk.get(2), 1.0);

        chunk.set(3, Type.LONG, 1000l);
        Assert.assertEquals(chunk.get(3), 1000l);

        chunk.set(4, Type.INT, 100);
        Assert.assertEquals(chunk.get(4), 100);

        chunk.set(5, Type.INT, 1);
        Assert.assertEquals(chunk.get(5), 1);

        chunk.set(5, Type.INT, null);
        Assert.assertEquals(chunk.get(5), null);

        Buffer buffer = factory.newBuffer();
        chunk.save(buffer);
        StateChunk chunk2 = (StateChunk) space.createAndMark(ChunkType.STATE_CHUNK, 0, 0, 1);
        chunk2.load(buffer);
        Buffer buffer2 = factory.newBuffer();
        chunk2.save(buffer2);

        Assert.assertTrue(compareBuffers(buffer, buffer2));

        for (int i = 0; i < 5; i++) {
            Assert.assertEquals(chunk.get(0), chunk2.get(0));
        }

        //init chunk selectWith arrays
        chunk.set(5, Type.LONG_ARRAY, new long[]{0, 1, 2, 3, 4});
        chunk.set(6, Type.DOUBLE_ARRAY, new double[]{0.1, 1.1, 2.1, 3.1, 4.1});
        chunk.set(7, Type.INT_ARRAY, new int[]{0, 1, 2, 3, 4});

        buffer.free();
        buffer = factory.newBuffer();

        chunk.save(buffer);
        space.free(chunk2);
        chunk2 = (StateChunk) space.createAndMark(ChunkType.STATE_CHUNK, 0, 0, 2);
        chunk2.load(buffer);

        buffer2.free();
        buffer2 = factory.newBuffer();
        chunk2.save(buffer2);

        Assert.assertTrue(compareBuffers(buffer, buffer2));

        //init chunk selectWith some maps
        LongLongMap long2longMap = (LongLongMap) chunk.getOrCreate(8, Type.LONG_TO_LONG_MAP);
        long2longMap.put(1, 1);
        long2longMap.put(Constants.END_OF_TIME, Constants.END_OF_TIME);
        long2longMap.put(Constants.BEGINNING_OF_TIME, Constants.BEGINNING_OF_TIME);

        StringLongMap string2longMap = (StringLongMap) chunk.getOrCreate(9, Type.STRING_TO_LONG_MAP);
        string2longMap.put("1", 1);
        string2longMap.put(Constants.END_OF_TIME + "", Constants.END_OF_TIME);
        string2longMap.put(Constants.BEGINNING_OF_TIME + "", Constants.BEGINNING_OF_TIME);

        LongLongArrayMap long2longArrayMap = (LongLongArrayMap) chunk.getOrCreate(10, Type.LONG_TO_LONG_ARRAY_MAP);
        long2longArrayMap.put(1, 1);
        long2longArrayMap.put(Constants.END_OF_TIME, Constants.END_OF_TIME);
        long2longArrayMap.put(Constants.BEGINNING_OF_TIME, Constants.BEGINNING_OF_TIME);
        long2longArrayMap.put(Constants.BEGINNING_OF_TIME, Constants.END_OF_TIME);

        buffer.free();
        buffer = factory.newBuffer();
        chunk.save(buffer);
        space.free(chunk2);
        chunk2 = (StateChunk) space.createAndMark(ChunkType.STATE_CHUNK, 0, 0, 3);
        chunk2.load(buffer);

        buffer2.free();
        buffer2 = factory.newBuffer();
        chunk2.save(buffer2);

        Assert.assertTrue(((StringLongMap) chunk2.get(9)).size() == 3);
        Assert.assertTrue(((LongLongMap) chunk2.get(8)).size() == 3);
        Assert.assertTrue(((LongLongArrayMap) chunk2.get(10)).size() == 4);

        Assert.assertTrue(compareBuffers(buffer, buffer2));
        // Assert.assertTrue(1 == nbCount);

        //force reHash
        for (int i = 0; i < 10; i++) {
            chunk.set(1000 + i, Type.INT, i);
        }

        for (int i = 0; i < 10; i++) {
            Assert.assertEquals(chunk.get(1000 + i), i);
        }

        StateChunk chunk3 = (StateChunk) space.createAndMark(ChunkType.STATE_CHUNK, 0, 0, 4);
        chunk3.loadFrom(chunk);
        Buffer buffer3 = factory.newBuffer();
        chunk3.save(buffer3);

        buffer.free();
        buffer = factory.newBuffer();
        chunk.save(buffer);
        Assert.assertTrue(compareBuffers(buffer, buffer3));

        buffer3.free();
        buffer2.free();
        buffer.free();

        space.free(chunk3);
        space.free(chunk2);
        space.free(chunk);

        //create an empty
        StateChunk chunk4 = (StateChunk) space.createAndMark(ChunkType.STATE_CHUNK, 0, 0, 5);
        chunk4.set(0, Type.LONG_ARRAY, new long[0]);
        Buffer saved4 = factory.newBuffer();
        chunk4.save(saved4);

        StateChunk chunk5 = (StateChunk) space.createAndMark(ChunkType.STATE_CHUNK, 0, 0, 6);
        chunk5.load(saved4);
        Assert.assertEquals(((long[]) chunk5.get(0)).length, 0);
        space.free(chunk5);
        space.free(chunk4);
        saved4.free();
        space.freeAll();
    }

    @Test
    public void cloneTest() {
        ChunkSpace space = factory.newSpace(100, null);
        StateChunk chunk = (StateChunk) space.createAndMark(ChunkType.STATE_CHUNK, 0, 0, 0);

        //init primitives
        chunk.set(0, Type.BOOL, true);
        chunk.set(1, Type.STRING, "hello");
        chunk.set(2, Type.LONG, 1000l);
        chunk.set(3, Type.INT, 100);
        chunk.set(4, Type.DOUBLE, 1.0);
        //init arrays
        chunk.set(5, Type.DOUBLE_ARRAY, new double[]{1.0, 2.0, 3.0});
        chunk.set(6, Type.LONG_ARRAY, new long[]{1, 2, 3});
        chunk.set(7, Type.INT_ARRAY, new int[]{1, 2, 3});
        //init maps
        ((LongLongMap) chunk.getOrCreate(8, Type.LONG_TO_LONG_MAP)).put(100, 100);
        ((LongLongArrayMap) chunk.getOrCreate(9, Type.LONG_TO_LONG_ARRAY_MAP)).put(100, 100);
        ((StringLongMap) chunk.getOrCreate(10, Type.STRING_TO_LONG_MAP)).put("100", 100);

        //clone the chunk
        StateChunk chunk2 = (StateChunk) space.createAndMark(ChunkType.STATE_CHUNK, 0, 0, 1);
        chunk2.loadFrom(chunk);
        //test primitives
        Assert.assertTrue(chunk2.getType(0) == Type.BOOL);
        Assert.assertTrue((Boolean) chunk.get(0));

        Assert.assertTrue(chunk2.getType(1) == Type.STRING);
        Assert.assertTrue(HashHelper.equals(chunk2.get(1).toString(), "hello"));

        Assert.assertTrue(chunk2.getType(2) == Type.LONG);
        Assert.assertTrue((Long) chunk2.get(2) == 1000l);

        Assert.assertTrue(chunk2.getType(3) == Type.INT);
        Assert.assertTrue((Integer) chunk2.get(3) == 100);

        Assert.assertTrue(chunk2.getType(4) == Type.DOUBLE);
        Assert.assertTrue((Double) chunk2.get(4) == 1.0);

        //test arrays
        Assert.assertTrue(chunk2.getType(5) == Type.DOUBLE_ARRAY);
        Assert.assertTrue(((double[]) chunk2.get(5))[0] == 1.0);

        Assert.assertTrue(chunk2.getType(6) == Type.LONG_ARRAY);
        Assert.assertTrue(((long[]) chunk2.get(6))[0] == 1);

        Assert.assertTrue(chunk2.getType(7) == Type.INT_ARRAY);
        Assert.assertTrue(((int[]) chunk2.get(7))[0] == 1);

        //test maps
        Assert.assertTrue(((LongLongMap) chunk2.get(8)).get(100) == 100);
        Assert.assertTrue(((LongLongArrayMap) chunk2.get(9)).get(100)[0] == 100);
        Assert.assertTrue(((StringLongMap) chunk2.get(10)).getValue("100") == 100);

        //now we test the co-evolution of clone

        //STRINGS
        chunk.set(1, Type.STRING, "helloPast");
        Assert.assertTrue(HashHelper.equals(chunk.get(1).toString(), "helloPast"));
        Assert.assertTrue(HashHelper.equals(chunk2.get(1).toString(), "hello"));

        chunk2.set(1, Type.STRING, "helloFuture");
        Assert.assertTrue(HashHelper.equals(chunk2.get(1).toString(), "helloFuture"));
        Assert.assertTrue(HashHelper.equals(chunk.get(1).toString(), "helloPast"));

        //ARRAYS
        chunk2.set(5, Type.DOUBLE_ARRAY, new double[]{3.0, 4.0, 5.0});
        Assert.assertTrue(((double[]) chunk2.get(5))[0] == 3.0);
        Assert.assertTrue(((double[]) chunk.get(5))[0] == 1.0);

        chunk2.set(6, Type.LONG_ARRAY, new long[]{100, 200, 300});
        Assert.assertTrue(((long[]) chunk2.get(6))[0] == 100);
        Assert.assertTrue(((long[]) chunk.get(6))[0] == 1);

        chunk2.set(7, Type.INT_ARRAY, new int[]{100, 200, 300});
        Assert.assertTrue(((int[]) chunk2.get(7))[0] == 100);
        Assert.assertTrue(((int[]) chunk.get(7))[0] == 1);

        //MAPS
        ((LongLongMap) chunk2.get(8)).put(100, 200);
        Assert.assertTrue(((LongLongMap) chunk2.get(8)).get(100) == 200);
        Assert.assertTrue(((LongLongMap) chunk.get(8)).get(100) == 100);

        ((LongLongArrayMap) chunk2.get(9)).put(100, 200);
        Assert.assertTrue(((LongLongArrayMap) chunk2.get(9)).get(100)[0] == 200);
        Assert.assertTrue(((LongLongArrayMap) chunk2.get(9)).get(100)[1] == 100);
        Assert.assertTrue(((LongLongArrayMap) chunk.get(9)).get(100)[0] == 100);

        ((StringLongMap) chunk2.get(10)).put("100", 200);
        Assert.assertTrue(((StringLongMap) chunk2.get(10)).getValue("100") == 200);
        Assert.assertTrue(((StringLongMap) chunk.get(10)).getValue("100") == 100);

        // add something new instead of replacing something -> triggers the shallow copy of the clone
        chunk2.set(11, Type.STRING, "newString");
        Assert.assertEquals(chunk2.get(11), "newString");

        space.free(chunk);
        space.free(chunk2);
        space.freeAll();

    }

    @Test
    public void protectionTest() {

        ChunkSpace space = factory.newSpace(100, null);
        StateChunk chunk = (StateChunk) space.createAndMark(ChunkType.STATE_CHUNK, 0, 0, 0);

        //boolean protection test
        //protectionMethod(chunk, Type.BOOL, null, true);
        protectionMethod(chunk, Type.BOOL, true, false);
        protectionMethod(chunk, Type.BOOL, "Hello", true);

        //protectionMethod(chunk, Type.DOUBLE, null, true);
        protectionMethod(chunk, Type.DOUBLE, 0.5d, false);
        protectionMethod(chunk, Type.DOUBLE, "Hello", true);

        //protectionMethod(chunk, Type.LONG, null, true);
        protectionMethod(chunk, Type.LONG, 100000000l, false);
        protectionMethod(chunk, Type.LONG, 100000000, false);
        protectionMethod(chunk, Type.LONG, "Hello", true);

        //protectionMethod(chunk, Type.INT, null, true);
        protectionMethod(chunk, Type.INT, 10, false);
        protectionMethod(chunk, Type.INT, "Hello", true);

        //protectionMethod(chunk, Type.STRING, null, false);
        protectionMethod(chunk, Type.STRING, "Hello", false);
        protectionMethod(chunk, Type.STRING, true, true);

        //arrays
        protectionMethod(chunk, Type.DOUBLE_ARRAY, new double[]{0.1d, 0.2d, 0.3d}, false);
        protectionMethod(chunk, Type.DOUBLE_ARRAY, "hello", true);

        protectionMethod(chunk, Type.LONG_ARRAY, new long[]{10l, 100l, 1000l}, false);
        protectionMethod(chunk, Type.LONG_ARRAY, "hello", true);

        protectionMethod(chunk, Type.INT_ARRAY, new int[]{10, 100, 1000}, false);
        protectionMethod(chunk, Type.INT_ARRAY, "hello", true);

        //maps
        protectionMethod(chunk, Type.STRING_TO_LONG_MAP, "hello", true);
        protectionMethod(chunk, Type.LONG_TO_LONG_MAP, "hello", true);
        protectionMethod(chunk, Type.LONG_TO_LONG_ARRAY_MAP, "hello", true);

        space.free(chunk);
        space.freeAll();


    }

    @Test
    public void typeSwitchTest() {
        ChunkSpace space = factory.newSpace(100, null);
        StateChunk chunk = (StateChunk) space.createAndMark(ChunkType.STATE_CHUNK, 0, 0, 0);

        //init primitives
        chunk.set(0, Type.BOOL, true);
        chunk.set(1, Type.STRING, "hello");
        chunk.set(2, Type.LONG, 1000l);
        chunk.set(3, Type.INT, 100);
        chunk.set(4, Type.DOUBLE, 1.0);
        //init arrays
        chunk.set(5, Type.DOUBLE_ARRAY, new double[]{1.0, 2.0, 3.0});
        chunk.set(6, Type.LONG_ARRAY, new long[]{1, 2, 3});
        chunk.set(7, Type.INT_ARRAY, new int[]{1, 2, 3});
        //init maps
        ((LongLongMap) chunk.getOrCreate(8, Type.LONG_TO_LONG_MAP)).put(100, 100);
        ((LongLongArrayMap) chunk.getOrCreate(9, Type.LONG_TO_LONG_ARRAY_MAP)).put(100, 100);
        ((StringLongMap) chunk.getOrCreate(10, Type.STRING_TO_LONG_MAP)).put("100", 100);

        //ok now switch all types

        //switch primitives
        chunk.set(10, Type.BOOL, true);
        Assert.assertTrue(chunk.getType(10) == Type.BOOL);
        Assert.assertTrue((Boolean) chunk.get(10));

        chunk.set(0, Type.STRING, "hello");
        Assert.assertTrue(chunk.getType(0) == Type.STRING);
        Assert.assertTrue(HashHelper.equals(chunk.get(0).toString(), "hello"));

        chunk.set(1, Type.LONG, 1000l);
        Assert.assertTrue(chunk.getType(1) == Type.LONG);
        Assert.assertTrue((Long) chunk.get(1) == 1000l);

        chunk.set(2, Type.INT, 100);
        Assert.assertTrue(chunk.getType(2) == Type.INT);
        Assert.assertTrue((Integer) chunk.get(2) == 100);

        chunk.set(3, Type.DOUBLE, 1.0);
        Assert.assertTrue(chunk.getType(3) == Type.DOUBLE);
        Assert.assertTrue((Double) chunk.get(3) == 1.0);

        //switch arrays
        chunk.set(4, Type.DOUBLE_ARRAY, new double[]{1.0, 2.0, 3.0});
        Assert.assertTrue(chunk.getType(4) == Type.DOUBLE_ARRAY);
        Assert.assertTrue(((double[]) chunk.get(4))[0] == 1.0);

        chunk.set(5, Type.LONG_ARRAY, new long[]{1, 2, 3});
        Assert.assertTrue(chunk.getType(5) == Type.LONG_ARRAY);
        Assert.assertTrue(((long[]) chunk.get(5))[0] == 1);

        chunk.set(6, Type.INT_ARRAY, new int[]{1, 2, 3});
        Assert.assertTrue(chunk.getType(6) == Type.INT_ARRAY);
        Assert.assertTrue(((int[]) chunk.get(6))[0] == 1);

        //switch maps
        ((LongLongMap) chunk.getOrCreate(7, Type.LONG_TO_LONG_MAP)).put(100, 100);
        ((LongLongArrayMap) chunk.getOrCreate(8, Type.LONG_TO_LONG_ARRAY_MAP)).put(100, 100);
        ((StringLongMap) chunk.getOrCreate(9, Type.STRING_TO_LONG_MAP)).put("100", 100);

        space.free(chunk);
        space.freeAll();

    }

    /*
    @Test
    public void loadTest() {
        ChunkSpace space = factory.newSpace(100, null);
        StateChunk chunk = (StateChunk) space.createAndMark(ChunkType.STATE_CHUNK, 0, 0, 0);

        Buffer buffer = factory.newBuffer();
        buffer.writeAll("G|uwt/siomw,E,cm9vbTA|Zdk+o9BKZ,Y,C:G|lQYzlsJcx,Y,G:M:W:e".getBytes());
        chunk.load(buffer);

        space.free(chunk);
        space.freeAll();
    }*/

    // G|uwt/siomw,E,cm9vbTA|Zdk+o9BKZ,Y,C:G|lQYzlsJcx,Y,G:M:W:e


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

    private void protectionMethod(StateChunk chunk, byte elemType, Object elem, boolean shouldCrash) {
        boolean hasCrash = false;
        try {
            chunk.set(0, elemType, elem);
        } catch (Throwable e) {
            hasCrash = true;
        }
        Assert.assertTrue(hasCrash == shouldCrash);
    }

}
