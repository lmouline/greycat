package org.mwg.memory.offheap;

import org.mwg.Constants;
import org.mwg.memory.offheap.primary.OffHeapLongArray;
import org.mwg.struct.Buffer;
import org.mwg.struct.Relationship;
import org.mwg.utility.Base64;
import org.mwg.utility.Unsafe;

@SuppressWarnings("Duplicates")
class OffHeapRelationship implements Relationship {

    private static final sun.misc.Unsafe unsafe = Unsafe.getUnsafe();

    private static int CAPACITY = 0;
    private static int SIZE = 1;
    private static int SHIFT = 2;

    private final long index;
    private final OffHeapStateChunk chunk;

    OffHeapRelationship(final OffHeapStateChunk p_chunk, final long p_index) {
        chunk = p_chunk;
        index = p_index;
    }

    public final void allocate(int newCapacity) {
        chunk.lock();
        try {
            final long addr = chunk.addrByIndex(index);
            if (addr == OffHeapConstants.OFFHEAP_NULL_PTR) {
                //initial allocation
                final long newly = OffHeapLongArray.allocate(newCapacity + SHIFT);
                OffHeapLongArray.set(newly, SIZE, 0);
                OffHeapLongArray.set(newly, CAPACITY, newCapacity);
                chunk.setAddrByIndex(index, newly);
            } else {
                final long capacity = OffHeapLongArray.get(addr, CAPACITY);
                if (capacity < newCapacity) {
                    //extends
                    long exAddr = OffHeapLongArray.reallocate(addr, newCapacity + SHIFT);
                    chunk.setAddrByIndex(index, exAddr);
                    OffHeapLongArray.set(exAddr, CAPACITY, newCapacity);
                }
            }
        } finally {
            chunk.unlock();
        }
    }

    @Override
    public final int size() {
        chunk.lock();
        long size = 0;
        try {
            final long addr = chunk.addrByIndex(index);
            if (addr != OffHeapConstants.OFFHEAP_NULL_PTR) {
                size = OffHeapLongArray.get(addr, SIZE);
            }
        } finally {
            chunk.unlock();
        }
        return (int) size;
    }

    @Override
    public final long get(final int elemIndex) {
        long result = -1;
        chunk.lock();
        try {
            final long addr = chunk.addrByIndex(index);
            if (addr != OffHeapConstants.OFFHEAP_NULL_PTR) {
                final long size = OffHeapLongArray.get(addr, SIZE);
                if (index < size) {
                    result = OffHeapLongArray.get(addr, elemIndex + SHIFT);
                } else {
                    return -1;
                }
            }
        } finally {
            chunk.unlock();
        }
        return result;
    }

    @Override
    public final Relationship add(final long newValue) {
        chunk.lock();
        try {
            internal_add(newValue);
            chunk.declareDirty();
        } finally {
            chunk.unlock();
        }
        return this;
    }

    final void internal_add(final long newValue) {
        long addr = chunk.addrByIndex(index);
        long size;
        if (addr == OffHeapConstants.OFFHEAP_NULL_PTR) {
            addr = OffHeapLongArray.allocate(Constants.MAP_INITIAL_CAPACITY + SHIFT);
            chunk.setAddrByIndex(index, addr);
            size = 0;
        } else {
            size = OffHeapLongArray.get(addr, SIZE);
            final long capacity = OffHeapLongArray.get(addr, SIZE);
            if (size == capacity) {
                final long newCapacity = capacity * 2;
                addr = OffHeapLongArray.reallocate(addr, newCapacity + SHIFT);
                chunk.setAddrByIndex(index, addr);
                OffHeapLongArray.set(addr, CAPACITY, newCapacity);
            }
        }
        OffHeapLongArray.set(addr, size + SHIFT, newValue);
        OffHeapLongArray.set(addr, SIZE, size + 1);
    }

    @Override
    public final Relationship remove(final long oldValue) {
        boolean leftShift = false;
        chunk.lock();
        try {
            long addr = chunk.addrByIndex(index);
            if (addr != OffHeapConstants.OFFHEAP_NULL_PTR) {
                final long size = OffHeapLongArray.get(addr, SIZE);
                for (int i = 0; i < size; i++) {
                    long current = OffHeapLongArray.get(addr, i);
                    if (leftShift) {
                        OffHeapLongArray.set(addr, index + (SHIFT - 1), current);
                    } else {
                        if (current == oldValue) {
                            leftShift = true;
                        }
                    }
                }
                if (leftShift) {
                    OffHeapLongArray.set(addr, SIZE, size - 1);
                    chunk.declareDirty();
                }
            }

        } finally {
            chunk.unlock();
        }
        return this;
    }

    @Override
    public final Relationship clear() {
        chunk.lock();
        try {
            final long addr = chunk.addrByIndex(index);
            OffHeapLongArray.set(addr, SIZE, 0);
        } finally {
            chunk.unlock();
        }
        return this;
    }

    @Override
    public final String toString() {
        StringBuilder buffer = new StringBuilder();
        chunk.lock();
        try {
            final long addr = chunk.addrByIndex(index);
            if (addr != OffHeapConstants.OFFHEAP_NULL_PTR) {
                buffer.append("[");
                final long size = OffHeapLongArray.get(addr, SIZE);
                for (int i = 0; i < size; i++) {
                    if (i != 0) {
                        buffer.append(",");
                    }
                    buffer.append(OffHeapLongArray.get(addr, i + SHIFT));
                }
                buffer.append("]");
            }
        } finally {
            chunk.unlock();
        }
        return buffer.toString();
    }

    static void save(final long addr, final Buffer buffer) {
        if (addr == OffHeapConstants.OFFHEAP_NULL_PTR) {
            return;
        }
        final long size = OffHeapLongArray.get(addr, SIZE);
        Base64.encodeLongToBuffer(size, buffer);
        for (long i = 0; i < size; i++) {
            buffer.write(Constants.CHUNK_SUB_SUB_SEP);
            Base64.encodeLongToBuffer(OffHeapLongArray.get(addr, i), buffer);
        }
    }

    static long clone(final long addr) {
        if (addr == OffHeapConstants.OFFHEAP_NULL_PTR) {
            return OffHeapConstants.OFFHEAP_NULL_PTR;
        }
        final long capacity = OffHeapLongArray.get(addr, CAPACITY);
        long newAddr = unsafe.allocateMemory(capacity * 8);
        unsafe.copyMemory(addr, newAddr, capacity * 8);
        return newAddr;
    }

    static void free(final long addr) {
        OffHeapLongArray.free(addr);
    }

}
