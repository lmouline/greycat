package org.mwg.memory.offheap;

public class OffHeapStringArray {

    public static long allocate(final long capacity) {
        return OffHeapLongArray.allocate(capacity);
    }

    public static long reallocate(final long addr, final long nextCapacity, final long currentCapacity) {
        long newAddr =  OffHeapLongArray.reallocate(addr, nextCapacity);
        OffHeapLongArray.reset((newAddr + currentCapacity) * 8, (nextCapacity - currentCapacity) * 8);
        return newAddr;
    }

    public static void set(final long addr, final long index, final String valueToInsert) {
        long stringPtr = OffHeapLongArray.get(addr, index);
        if (stringPtr != OffHeapConstants.OFFHEAP_NULL_PTR) {
            OffHeapString.free(stringPtr);
        }
        stringPtr = OffHeapString.fromObject(valueToInsert);
        OffHeapLongArray.set(addr, index, stringPtr);
    }

    public static String get(final long addr, final long index) {
        long stringPtr = OffHeapLongArray.get(addr, index);
        if (stringPtr == OffHeapConstants.OFFHEAP_NULL_PTR) {
            return null;
        }
        return OffHeapString.asObject(addr);
    }

    public static void free(final long addr, final long capacity) {
        for (long i = 0; i < capacity; i++) {
            long stringPtr = OffHeapLongArray.get(addr, i);
            if (stringPtr != OffHeapConstants.OFFHEAP_NULL_PTR) {
                OffHeapString.free(stringPtr);
            }
        }
        OffHeapLongArray.free(addr);
    }

    public static long cloneArray(final long srcAddr, final long length) {
        // this is just a shallow copy... (i.e., only the root array is copied)
        return OffHeapLongArray.cloneArray(srcAddr, length);
    }


}
