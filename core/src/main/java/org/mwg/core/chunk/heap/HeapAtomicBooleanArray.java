package org.mwg.core.chunk.heap;

import org.mwg.utility.Unsafe;

import java.util.Arrays;

public class HeapAtomicBooleanArray {

    /**
     * @ignore ts
     */
    private static final sun.misc.Unsafe unsafe = Unsafe.getUnsafe();

    private static final int base = unsafe.arrayBaseOffset(boolean[].class);
    private static final int scale = unsafe.arrayIndexScale(boolean[].class);
    private final boolean[] _back;

    public HeapAtomicBooleanArray(int initialSize) {
        _back = new boolean[initialSize];
        Arrays.fill(_back, 0, initialSize, false);
    }

    public boolean volatileGet(int index) {
        return unsafe.getBooleanVolatile(_back, base + index * scale);
    }

    public void volatileSet(int index, boolean value) {
        unsafe.putBooleanVolatile(_back, base + index * scale, value);
    }

    public boolean get(int index) {
        return _back[index];
    }

    /*
    public boolean compareAndSwap(int index, boolean previousValue, boolean nextValue) {
        return unsafe.compareAndSwap(_back, base + index * scale, previousValue, nextValue);
    }*/

    /*
    public static void main(String[] args) {
        HeapAtomicLongArray ar = new HeapAtomicLongArray(10);
        ar._back[0] = Constants.END_OF_TIME;
        System.out.println(Constants.END_OF_TIME + "-" + ar.volatileGet(0));

        System.out.println(ar.volatileGet(1));

        System.out.println(ar.compareAndSwap(1, -1, 100));
        System.out.println(ar.volatileGet(1));
    }*/

}
