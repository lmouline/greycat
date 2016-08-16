package org.mwg.core.chunk.heap;

import org.mwg.utility.Unsafe;

public class HeapAtomicByteArray {

    /**
     * @ignore ts
     */
    private static final sun.misc.Unsafe unsafe = Unsafe.getUnsafe();
    /**
     * @ignore ts
     */
    private static final int base = unsafe.arrayBaseOffset(byte[].class);
    /**
     * @ignore ts
     */
    private static final int scale = unsafe.arrayIndexScale(byte[].class);

    private final byte[] _back;

    public HeapAtomicByteArray(int initialSize) {
        _back = new byte[initialSize];
    }

    /**
     * @native ts
     * return this._back[index];
     */
    public byte get(int index) {
        return unsafe.getByteVolatile(_back, base + index * scale);
    }

    /**
     * @native ts
     * this._back[index] = value;
     */
    public void set(int index, byte value) {
        unsafe.putByteVolatile(_back, base + index * scale, value);
    }

}
