package org.mwg;

public class NativeHasherHelper {

    static native long tripleHash(byte p0, long p1, long p2, long p3, long max);

    static {
        System.load("/Users/duke/dev/mwDB/plugins/native/src/main/resources/natives.dylib");
    }

    public static void main(String[] args) {
        long before = System.currentTimeMillis();
        long sum = 0;
        for (long i = 0; i < 1000000000; i++) {
            sum += tripleHash((byte) 0, i, i * 2, i * 3, 1000000000L);
        }
        long after = System.currentTimeMillis();
        System.out.println(sum+"/"+(after - before) + " ms");
    }

}
