package org.mwg.memory.offheap.primary;

import org.mwg.Constants;
import org.mwg.memory.offheap.OffHeapConstants;
import org.mwg.struct.Buffer;
import org.mwg.utility.Base64;
import org.mwg.utility.Unsafe;

public class OffHeapIntArray {

    private static int COW_INDEX = 0;
    private static int SIZE_INDEX = 1;
    private static int SHIFT_INDEX = 2;

    public static long alloc_counter = 0;

    private static final sun.misc.Unsafe unsafe = Unsafe.getUnsafe();

    public static long allocate(final long capacity) {
        //create the memory segment
        long newMemorySegment = unsafe.allocateMemory(capacity * 4);
        if (OffHeapConstants.DEBUG_MODE) {
            OffHeapConstants.SEGMENTS.put(newMemorySegment, capacity * 4);
        }
        //init the memory
        unsafe.setMemory(newMemorySegment, capacity * 4, (byte) OffHeapConstants.NULL_PTR);
        //return the newly created segment
        return newMemorySegment;
    }
    
    public static long reallocate(final long addr, final long nextCapacity) {
        long new_segment = unsafe.reallocateMemory(addr, nextCapacity * 4);
        if (OffHeapConstants.DEBUG_MODE) {
            OffHeapConstants.SEGMENTS.remove(addr);
            OffHeapConstants.SEGMENTS.put(new_segment, nextCapacity * 4);
        }
        return new_segment;
    }

    public static void set(final long addr, final long index, final int valueToInsert) {
        if (OffHeapConstants.DEBUG_MODE) {
            Long allocated = OffHeapConstants.SEGMENTS.get(addr);
            if (allocated == null || index < 0 || (index * 4) > allocated) {
                throw new RuntimeException("set: bad address " + index + "(" + index * 4 + ")" + " in " + allocated);
            }
        }
        unsafe.putIntVolatile(null, addr + index * 4, valueToInsert);
    }

    public static int get(final long addr, final long index) {
        if (OffHeapConstants.DEBUG_MODE) {
            Long allocated = OffHeapConstants.SEGMENTS.get(addr);
            if (allocated == null || index < 0 || (index * 4) > allocated) {
                throw new RuntimeException("get: bad address " + index + " in " + allocated);
            }
        }
        return unsafe.getIntVolatile(null, addr + index * 4);
    }

    public static void free(final long addr) {
        if (OffHeapConstants.DEBUG_MODE) {
            OffHeapConstants.SEGMENTS.remove(addr);
        }
        unsafe.freeMemory(addr);
    }

    static boolean compareAndSwap(final long addr, final long index, final int expectedValue, final int updatedValue) {
        return unsafe.compareAndSwapInt(null, addr + index * 4, expectedValue, updatedValue);
    }

    public static void save(final long addr, final Buffer buffer) {
        if (addr == OffHeapConstants.NULL_PTR) {
            return;
        }
        final long rawSize = OffHeapIntArray.get(addr, SIZE_INDEX);
        Base64.encodeLongToBuffer(rawSize, buffer);
        for (int j = 0; j < rawSize; j++) {
            buffer.write(Constants.CHUNK_VAL_SEP);
            Base64.encodeLongToBuffer(get(addr, j + SHIFT_INDEX), buffer);
        }
    }

    public static int[] asObject(final long addr) {
        if (addr == OffHeapConstants.NULL_PTR) {
            return null;
        }
        int longArrayLength = get(addr, SIZE_INDEX); // can be safely casted
        int[] longArray = new int[longArrayLength];
        for (int i = 0; i < longArrayLength; i++) {
            longArray[i] = get(addr, i + SHIFT_INDEX);
        }
        return longArray;
    }

    public static long fromObject(int[] origin) {
        long intArrayToInsert_ptr = OffHeapIntArray.allocate(SHIFT_INDEX + origin.length);
        set(intArrayToInsert_ptr, SIZE_INDEX, origin.length);
        set(intArrayToInsert_ptr, COW_INDEX, 1);
        for (int i = 0; i < origin.length; i++) {
            set(intArrayToInsert_ptr, SHIFT_INDEX + i, origin[i]);
        }
        return intArrayToInsert_ptr;
    }

    public static long cloneObject(final long addr) {
        int cow;
        int cow_after;
        do {
            cow = get(addr, COW_INDEX);
            cow_after = cow + 1;
        } while (!compareAndSwap(addr, COW_INDEX, cow, cow_after));
        return addr;
    }

    public static void freeObject(final long addr) {
        int cow;
        int cow_after;
        do {
            cow = get(addr, COW_INDEX);
            cow_after = cow - 1;
        } while (!compareAndSwap(addr, COW_INDEX, cow, cow_after));
        if (cow == 1 && cow_after == 0) {
            unsafe.freeMemory(addr);
            if (OffHeapConstants.DEBUG_MODE) {
                OffHeapConstants.SEGMENTS.remove(addr);
            }
        }
    }

}
