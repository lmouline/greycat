package org.mwg.memory.offheap;

import org.mwg.Constants;
import org.mwg.memory.offheap.primary.OffHeapByteArray;
import org.mwg.struct.Buffer;
import org.mwg.struct.BufferIterator;
import org.mwg.utility.DefaultBufferIterator;

class OffHeapBuffer implements Buffer {

    private long bufferPtr = OffHeapConstants.OFFHEAP_NULL_PTR;
    private long writeCursor = 0;
    private long capacity = 0;

    @Override
    public byte[] slice(long initPos, long endPos) {
        int newSize = (int) (endPos - initPos + 1);
        byte[] result = new byte[newSize];
        for (int i = 0; i < newSize; i++) {
            result[i] = OffHeapByteArray.get(bufferPtr, i + initPos);
        }
        return result;
    }

    @Override
    public void write(byte b) {
        if (bufferPtr == OffHeapConstants.OFFHEAP_NULL_PTR) {
            capacity = Constants.MAP_INITIAL_CAPACITY;
            bufferPtr = OffHeapByteArray.allocate(capacity);
            OffHeapByteArray.set(bufferPtr, writeCursor, b);
            writeCursor++;
        } else if (writeCursor == capacity) {
            long newCapacity = capacity * 2;
            bufferPtr = OffHeapByteArray.reallocate(bufferPtr, newCapacity);
            capacity = newCapacity;
            OffHeapByteArray.set(bufferPtr, writeCursor, b);
            writeCursor++;
        } else {
            OffHeapByteArray.set(bufferPtr, writeCursor, b);
            writeCursor++;
        }
    }

    @Override
    public void writeAll(byte[] bytes) {
        if (bufferPtr == OffHeapConstants.OFFHEAP_NULL_PTR) {
            capacity = getNewSize(Constants.MAP_INITIAL_CAPACITY, bytes.length);
            bufferPtr = OffHeapByteArray.allocate(capacity);
            OffHeapByteArray.copyArray(bytes, bufferPtr, bytes.length);
            writeCursor = bytes.length;
        } else if (writeCursor + bytes.length > capacity) {
            long newCapacity = getNewSize(capacity, capacity + bytes.length);
            bufferPtr = OffHeapByteArray.reallocate(bufferPtr, newCapacity);
            OffHeapByteArray.copyArray(bytes, bufferPtr + writeCursor, bytes.length);
            capacity = newCapacity;
            writeCursor = writeCursor + bytes.length;
        } else {
            OffHeapByteArray.copyArray(bytes, bufferPtr + writeCursor, bytes.length);
            writeCursor = writeCursor + bytes.length;
        }
    }

    @Override
    public byte read(long position) {
        if (bufferPtr != OffHeapConstants.OFFHEAP_NULL_PTR && position < capacity) {
            return OffHeapByteArray.get(bufferPtr, position);
        }
        return -1;
    }

    @Override
    public byte[] data() {
        byte[] result = new byte[(int) writeCursor];
        for (long i = 0; i < writeCursor; i++) {
            result[(int) i] = OffHeapByteArray.get(bufferPtr, i);
        }
        return result;
    }

    @Override
    public long length() {
        return writeCursor;
    }

    @Override
    public void free() {
        if (bufferPtr != OffHeapConstants.OFFHEAP_NULL_PTR) {
            OffHeapByteArray.free(bufferPtr);
            bufferPtr = OffHeapConstants.OFFHEAP_NULL_PTR;
            capacity = 0;
            writeCursor = 0;
        }
    }

    @Override
    public BufferIterator iterator() {
        return new DefaultBufferIterator(this);
    }

    @Override
    public void removeLast() {
        writeCursor--;
    }

    private long getNewSize(long old, long target) {
        while (old < target) {
            old = old * 2;
        }
        return old;
    }

    @Override
    public String toString() {
        return new String(data());
    }

}
