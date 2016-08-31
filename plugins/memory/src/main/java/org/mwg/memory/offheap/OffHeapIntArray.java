package org.mwg.memory.offheap;

import org.mwg.Constants;
import org.mwg.struct.Buffer;
import org.mwg.utility.Base64;
import org.mwg.utility.Unsafe;

class OffHeapIntArray {

    public static long alloc_counter = 0;

    private static final sun.misc.Unsafe unsafe = Unsafe.getUnsafe();

    public static long allocate(final long capacity) {
        if (Unsafe.DEBUG_MODE) {
            alloc_counter++;
        }
        //create the memory segment
        long newMemorySegment = unsafe.allocateMemory(capacity * 4);
        //init the memory
        unsafe.setMemory(newMemorySegment, capacity * 4, (byte) OffHeapConstants.OFFHEAP_NULL_PTR);
        //return the newly created segment
        return newMemorySegment;
    }

    public static void reset(final long addr, final long capacity) {
        unsafe.setMemory(addr, capacity * 4, (byte) OffHeapConstants.OFFHEAP_NULL_PTR);
    }

    public static long reallocate(final long addr, final long nextCapacity) {
        return unsafe.reallocateMemory(addr, nextCapacity * 4);
    }

    public static void set(final long addr, final long index, final long valueToInsert) {
        unsafe.putLongVolatile(null, addr + index * 4, valueToInsert);
    }

    public static int get(final long addr, final long index) {
        return unsafe.getIntVolatile(null, addr + index * 4);
    }

    public static void free(final long addr) {
        if (Unsafe.DEBUG_MODE) {
            alloc_counter--;
        }
        unsafe.freeMemory(addr);
    }

    public static boolean compareAndSwap(final long addr, final long index, final long expectedValue, final long updatedValue) {
        return unsafe.compareAndSwapLong(null, addr + index * 4, expectedValue, updatedValue);
    }

    public static long cloneArray(final long srcAddr, final long length) {
        alloc_counter++;
        long newAddr = unsafe.allocateMemory(length * 4);
        unsafe.copyMemory(srcAddr, newAddr, length * 4);
        return newAddr;
    }

    static void save(final long addr, final Buffer buffer) {
        if (addr == OffHeapConstants.OFFHEAP_NULL_PTR) {
            return;
        }
        final long rawSize = OffHeapIntArray.get(addr, 0);
        Base64.encodeLongToBuffer(rawSize, buffer);
        for (int j = 0; j < rawSize; j++) {
            buffer.write(Constants.CHUNK_SUB_SUB_SEP);
            Base64.encodeLongToBuffer(OffHeapIntArray.get(addr, j + 1), buffer);
        }
    }

    static int[] asObject(final long addr) {
        if (addr == OffHeapConstants.OFFHEAP_NULL_PTR) {
            return null;
        }
        int longArrayLength = OffHeapIntArray.get(addr, 0); // can be safely casted
        int[] longArray = new int[longArrayLength];
        for (int i = 0; i < longArrayLength; i++) {
            longArray[i] = OffHeapIntArray.get(addr, i + 1);
        }
        return longArray;
    }

    static long fromObject(int[] origin) {
        long intArrayToInsert_ptr = OffHeapIntArray.allocate(1 + origin.length); // length + content of the array
        OffHeapLongArray.set(intArrayToInsert_ptr, 0, origin.length);// set length
        for (int i = 0; i < origin.length; i++) {
            OffHeapIntArray.set(intArrayToInsert_ptr, 1 + i, origin[i]);
        }
        return intArrayToInsert_ptr;
    }

}
