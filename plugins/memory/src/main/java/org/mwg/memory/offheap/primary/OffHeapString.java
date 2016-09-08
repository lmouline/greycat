package org.mwg.memory.offheap.primary;

import org.mwg.memory.offheap.OffHeapConstants;
import org.mwg.struct.Buffer;
import org.mwg.utility.Base64;
import org.mwg.utility.Unsafe;

public class OffHeapString {

    private static int COW = 0;
    private static int SIZE = 8;
    private static int SHIFT = 12;

    public static long alloc_counter = 0;

    private static final sun.misc.Unsafe unsafe = Unsafe.getUnsafe();

    public static void save(final long addr, final Buffer buffer) {
        if (addr == OffHeapConstants.OFFHEAP_NULL_PTR) {
            return;
        }
        Base64.encodeStringToBuffer(asObject(addr), buffer);
    }

    public static long fromObject(String origin) {
        final byte[] valueAsByte = origin.getBytes();
        long newStringPtr = unsafe.allocateMemory(SHIFT + valueAsByte.length);
        unsafe.putLong(newStringPtr, 1);
        unsafe.putInt(newStringPtr + 8, valueAsByte.length);
        for (int i = 0; i < valueAsByte.length; i++) {
            unsafe.putByte(8 + 4 + newStringPtr + i, valueAsByte[i]);
        }
        if (Unsafe.DEBUG_MODE) {
            alloc_counter++;
        }
        return newStringPtr;
    }

    public static String asObject(final long addr) {
        if (addr == OffHeapConstants.OFFHEAP_NULL_PTR) {
            return null;
        }
        int length = unsafe.getInt(addr + SIZE);
        byte[] bytes = new byte[length];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = unsafe.getByte(addr + SHIFT + i);
        }
        return new String(bytes);
    }

    public static long clone(final long addr) {
        long cow;
        long cow_after;
        do {
            cow = unsafe.getLong(addr + COW);
            cow_after = cow + 1;
        } while (!unsafe.compareAndSwapLong(null, addr + COW, cow, cow_after));
        return addr;
    }

    public static void free(final long addr) {
        long cow;
        long cow_after;
        do {
            cow = unsafe.getLong(addr + COW);
            cow_after = cow - 1;
        } while (!unsafe.compareAndSwapLong(null, addr + COW, cow, cow_after));
        if (cow == 1 && cow_after == 0) {
            unsafe.freeMemory(addr);
            if (Unsafe.DEBUG_MODE) {
                alloc_counter--;
            }
        }
    }

}
