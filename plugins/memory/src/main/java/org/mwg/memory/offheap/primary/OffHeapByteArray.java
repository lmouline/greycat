package org.mwg.memory.offheap.primary;

import org.mwg.memory.offheap.OffHeapConstants;
import org.mwg.utility.Unsafe;

public class OffHeapByteArray {

    private static final sun.misc.Unsafe unsafe = Unsafe.getUnsafe();

    public static long allocate(final long capacity) {
        //create the memory segment
        long newMemorySegment = unsafe.allocateMemory(capacity);
        if (OffHeapConstants.DEBUG_MODE) {
            OffHeapConstants.SEGMENTS.put(newMemorySegment, capacity);
        }
        //init the memory
        unsafe.setMemory(newMemorySegment, capacity, (byte) OffHeapConstants.OFFHEAP_NULL_PTR);
        //return the newly created segment
        return newMemorySegment;
    }

    public static long reallocate(final long addr, final long nextCapacity) {
        long new_segment = unsafe.reallocateMemory(addr, nextCapacity);
        if (OffHeapConstants.DEBUG_MODE) {
            if(!OffHeapConstants.SEGMENTS.containsKey(addr)){
                throw new RuntimeException("Bad ADDR!");
            }
            OffHeapConstants.SEGMENTS.remove(addr);
            OffHeapConstants.SEGMENTS.put(new_segment, nextCapacity);
        }
        return new_segment;
    }

    /**
     * Transfer an primitive array from heap to off-heap memory
     *
     * @param src        Object to copy, should be an array
     * @param destAddr   start of address to store the source object
     * @param nbElements offset of destination address
     */
    public static void copyArray(final Object src, final long destAddr, final long index, final long nbElements) {
        int baseOffset = unsafe.arrayBaseOffset(src.getClass());
        int scaleOffset = unsafe.arrayIndexScale(src.getClass());
        if (OffHeapConstants.DEBUG_MODE) {
            Long allocated = OffHeapConstants.SEGMENTS.get(destAddr);
            if (allocated == null || destAddr < 0 || (nbElements * scaleOffset) > allocated) {
                throw new RuntimeException("set: bad address in " + allocated);
            }
        }
        unsafe.copyMemory(src, baseOffset, null, destAddr + index, nbElements * scaleOffset);
    }

    public static void set(final long addr, final long index, final byte valueToInsert) {
        if (OffHeapConstants.DEBUG_MODE) {
            Long allocated = OffHeapConstants.SEGMENTS.get(addr);
            if (allocated == null || index < 0 || (index) > allocated) {
                throw new RuntimeException("set: bad address " + index + "(" + index + ")" + " in " + allocated);
            }
        }
        unsafe.putByteVolatile(null, addr + index, valueToInsert);
    }

    public static byte get(final long addr, final long index) {
        if (OffHeapConstants.DEBUG_MODE) {
            Long allocated = OffHeapConstants.SEGMENTS.get(addr);
            if (allocated == null || index < 0 || (index) > allocated) {
                throw new RuntimeException("get: bad address " + index + " in " + allocated);
            }
        }
        return unsafe.getByteVolatile(null, addr + index);
    }

    public static void free(final long addr) {
        if (OffHeapConstants.DEBUG_MODE) {
            if(!OffHeapConstants.SEGMENTS.containsKey(addr)){
                throw new RuntimeException("Bad ADDR!");
            }
            OffHeapConstants.SEGMENTS.remove(addr);
        }
        unsafe.freeMemory(addr);
    }

}
