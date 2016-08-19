package org.mwg.memory.offheap;

import org.mwg.utility.Unsafe;

public class OffHeapByteArray {

    public static long alloc_counter = 0;

    private static final sun.misc.Unsafe unsafe = Unsafe.getUnsafe();

    public static long allocate(final long capacity) {
        if (Unsafe.DEBUG_MODE) {
            alloc_counter++;
        }

        //create the memory segment
        long newMemorySegment = unsafe.allocateMemory(capacity);
        //init the memory
        unsafe.setMemory(newMemorySegment, capacity, (byte) OffHeapConstants.OFFHEAP_NULL_PTR);
        //return the newly created segment
        return newMemorySegment;
    }

    public static long reallocate(final long addr, final long nextCapacity) {
        return unsafe.reallocateMemory(addr, nextCapacity);
    }

    /**
     * Transfer an primitive array from heap to off-heap memory
     *
     * @param src        Object to copy, should be an array
     * @param destAddr   start of address to store the source object
     * @param nbElements offset of destination address
     */
    public static void copyArray(final Object src, final long destAddr, final long nbElements) {
        int baseOffset = unsafe.arrayBaseOffset(src.getClass());
        int scaleOffset = unsafe.arrayIndexScale(src.getClass());

        unsafe.copyMemory(src, baseOffset, null, destAddr, nbElements * scaleOffset);
    }

    public static void set(final long addr, final long index, final byte valueToInsert) {
        unsafe.putByteVolatile(null, addr + index, valueToInsert);
    }

    public static byte get(final long addr, final long index) {
        return unsafe.getByteVolatile(null, addr + index);
    }

    public static void free(final long addr) {
        if (Unsafe.DEBUG_MODE) {
            alloc_counter--;
        }

        unsafe.freeMemory(addr);
    }

    /*
    public static long cloneArray(final long srcAddr, final long length) {
        if (Unsafe.DEBUG_MODE) {
            alloc_counter++;
        }

        long newAddr = unsafe.allocateMemory(length);
        unsafe.copyMemory(srcAddr, newAddr, length);
        return newAddr;
    }*/

}
