package org.mwg.memory.offheap;

import org.mwg.Constants;
import org.mwg.struct.Buffer;
import org.mwg.utility.Base64;
import org.mwg.utility.Unsafe;

public class OffHeapLongArray {

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

    static void reset(final long addr, final long capacity) {
        unsafe.setMemory(addr, capacity * 8, (byte) OffHeapConstants.OFFHEAP_NULL_PTR);
    }

    public static long reallocate(final long addr, final long nextCapacity) {
        return unsafe.reallocateMemory(addr, nextCapacity * 8);
    }

    public static void set(final long addr, final long index, final long valueToInsert) {
        unsafe.putLongVolatile(null, addr + index * 8, valueToInsert);
    }

    public static long get(final long addr, final long index) {
        return unsafe.getLongVolatile(null, addr + index * 8);
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

    static long cloneArray(final long srcAddr, final long length) {
        if (srcAddr == OffHeapConstants.OFFHEAP_NULL_PTR) {
            return srcAddr;
        }
        alloc_counter++;
        long newAddr = unsafe.allocateMemory(length * 8);
        unsafe.copyMemory(srcAddr, newAddr, length * 8);
        return newAddr;
    }

    static void save(final long addr, final Buffer buffer) {
        if (addr == OffHeapConstants.OFFHEAP_NULL_PTR) {
            return;
        }
        final long rawSize = OffHeapLongArray.get(addr, SIZE_INDEX);
        Base64.encodeLongToBuffer(rawSize, buffer);
        for (int j = 0; j < rawSize; j++) {
            buffer.write(Constants.CHUNK_SUB_SUB_SEP);
            Base64.encodeLongToBuffer(OffHeapLongArray.get(addr, j + SHIFT_INDEX), buffer);
        }
    }

    static long[] asObject(final long addr) {
        if (addr == OffHeapConstants.OFFHEAP_NULL_PTR) {
            return null;
        }
        int longArrayLength = (int) get(addr, SIZE_INDEX); // can be safely casted
        long[] longArray = new long[longArrayLength];
        for (int i = 0; i < longArrayLength; i++) {
            longArray[i] = get(addr, i + SHIFT_INDEX);
        }
        return longArray;
    }

    static long fromObject(long[] origin) {
        long longArrayToInsert_ptr = allocate(SHIFT_INDEX + origin.length);
        set(longArrayToInsert_ptr, SIZE_INDEX, origin.length);
        set(longArrayToInsert_ptr, COW_INDEX, 1);
        for (int i = 0; i < origin.length; i++) {
            set(longArrayToInsert_ptr, SHIFT_INDEX + i, origin[i]);
        }
        return longArrayToInsert_ptr;
    }

    static long cloneObject(final long addr) {
        long cow;
        long cow_after;
        do {
            cow = get(addr, COW_INDEX);
            cow_after = cow + 1;
        } while (!compareAndSwap(addr, COW_INDEX, cow, cow_after));
        return addr;
    }

    static void freeObject(final long addr) {
        long cow;
        long cow_after;
        do {
            cow = get(addr, COW_INDEX);
            cow_after = cow - 1;
        } while (!compareAndSwap(addr, COW_INDEX, cow, cow_after));
        if (cow == 1 && cow_after == 0) {
            unsafe.freeMemory(addr);
            if (Unsafe.DEBUG_MODE) {
                alloc_counter--;
            }
        }
    }


}
