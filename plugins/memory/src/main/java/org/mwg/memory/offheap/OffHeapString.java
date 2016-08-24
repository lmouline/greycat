package org.mwg.memory.offheap;

import org.mwg.struct.Buffer;
import org.mwg.utility.Base64;
import org.mwg.utility.Unsafe;

public class OffHeapString {

    private static int COW = 0;
    private static int SIZE = 8;
    private static int SHIFT = 12;

    private static final sun.misc.Unsafe unsafe = Unsafe.getUnsafe();

    static void save(final long addr, final Buffer buffer) {
        if (addr == OffHeapConstants.OFFHEAP_NULL_PTR) {
            return;
        }
        Base64.encodeStringToBuffer(asObject(addr), buffer);
    }

    static String asObject(final long addr) {
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

    static long clone(final long addr) {
        long cow;
        long cow_after;
        do {
            cow = unsafe.getLong(addr + COW);
            cow_after = cow + 1;
        } while (!unsafe.compareAndSwapLong(null, addr + COW, cow, cow_after));
        return addr;
    }

    static void free(final long addr) {
        long cow;
        long cow_after;
        do {
            cow = unsafe.getLong(addr + COW);
            cow_after = cow - 1;
        } while (!unsafe.compareAndSwapLong(null, addr + COW, cow, cow_after));
        if (cow == 1 && cow_after == 0) {
            OffHeapByteArray.free(addr);
        }
    }

}
