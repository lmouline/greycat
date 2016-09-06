package org.mwg.memory.offheap.primary;

import org.mwg.Constants;
import org.mwg.memory.offheap.OffHeapConstants;
import org.mwg.struct.Buffer;
import org.mwg.utility.Base64;
import org.mwg.utility.Unsafe;

public class OffHeapDoubleArray {

    private static int COW_INDEX = 0;
    private static int SIZE_INDEX = 1;
    private static int SHIFT_INDEX = 2;

    public static long alloc_counter = 0;

    private static final sun.misc.Unsafe unsafe = Unsafe.getUnsafe();

    public static long allocate(final long capacity) {
        if (Unsafe.DEBUG_MODE) {
            alloc_counter++;
        }
        //create the memory segment
        long newMemorySegment = unsafe.allocateMemory(capacity * 8);
        //init the memory
        unsafe.setMemory(newMemorySegment, capacity * 8, (byte) OffHeapConstants.OFFHEAP_NULL_PTR);
        //return the newly created segment
        return newMemorySegment;
    }

    public static long reallocate(final long addr, final long nextCapacity) {
        return unsafe.reallocateMemory(addr, nextCapacity * 8);
    }

    public static void set(final long addr, final long index, final double valueToInsert) {
        unsafe.putDoubleVolatile(null, addr + index * 8, valueToInsert);
    }

    public static double get(final long addr, final long index) {
        return unsafe.getDoubleVolatile(null, addr + index * 8);
    }

    public static void free(final long addr) {
        if (Unsafe.DEBUG_MODE) {
            alloc_counter--;
        }

        unsafe.freeMemory(addr);
    }

    public static boolean compareAndSwap(final long addr, final long index, final long expectedValue, final long updatedValue) {
        return unsafe.compareAndSwapLong(null, addr + index * 8, expectedValue, updatedValue);
    }

    public static void copy(final long srcAddr, final long destAddr, long numberOfElemsToCopy) {
        unsafe.copyMemory(srcAddr, destAddr, numberOfElemsToCopy);
    }

    public static long cloneArray(final long srcAddr, final long length) {
        if (Unsafe.DEBUG_MODE) {
            alloc_counter++;
        }

        long newAddr = unsafe.allocateMemory(length * 8);
        unsafe.copyMemory(srcAddr, newAddr, length * 8);
        return newAddr;
    }

    public static void save(final long addr, final Buffer buffer) {
        if (addr == OffHeapConstants.OFFHEAP_NULL_PTR) {
            return;
        }
        int rawSize = (int) OffHeapLongArray.get(addr, SIZE_INDEX);
        Base64.encodeIntToBuffer(rawSize, buffer);
        for (int j = 0; j < rawSize; j++) {
            buffer.write(Constants.CHUNK_SUB_SUB_SEP);
            Base64.encodeDoubleToBuffer(OffHeapDoubleArray.get(addr, j + SHIFT_INDEX), buffer);
        }
    }

    public static double[] asObject(final long addr) {
        if (addr == OffHeapConstants.OFFHEAP_NULL_PTR) {
            return null;
        }
        int doubleArrayLength = (int) OffHeapLongArray.get(addr, SIZE_INDEX);
        double[] doubleArray = new double[doubleArrayLength];
        for (int i = 0; i < doubleArrayLength; i++) {
            doubleArray[i] = OffHeapDoubleArray.get(addr, i + SHIFT_INDEX);
        }
        return doubleArray;
    }

    public static long fromObject(double[] origin) {
        long doubleArrayToInsert_ptr = OffHeapDoubleArray.allocate(SHIFT_INDEX + origin.length);
        OffHeapLongArray.set(doubleArrayToInsert_ptr, SIZE_INDEX, origin.length);
        OffHeapLongArray.set(doubleArrayToInsert_ptr, COW_INDEX, 1);
        for (int i = 0; i < origin.length; i++) {
            OffHeapDoubleArray.set(doubleArrayToInsert_ptr, SHIFT_INDEX + i, origin[i]);
        }
        return doubleArrayToInsert_ptr;
    }

    public static long cloneObject(final long addr) {
        long cow;
        long cow_after;
        do {
            cow = OffHeapLongArray.get(addr, COW_INDEX);
            cow_after = cow + 1;
        } while (!OffHeapLongArray.compareAndSwap(addr, COW_INDEX, cow, cow_after));
        return addr;
    }

    public static void freeObject(final long addr) {
        long cow;
        long cow_after;
        do {
            cow = OffHeapLongArray.get(addr, COW_INDEX);
            cow_after = cow - 1;
        } while (!OffHeapLongArray.compareAndSwap(addr, COW_INDEX, cow, cow_after));
        if (cow == 1 && cow_after == 0) {
            unsafe.freeMemory(addr);
            if (Unsafe.DEBUG_MODE) {
                alloc_counter--;
            }
        }
    }

}
