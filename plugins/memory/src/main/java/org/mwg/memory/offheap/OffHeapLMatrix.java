package org.mwg.memory.offheap;

import org.mwg.Constants;
import org.mwg.memory.offheap.primary.OffHeapLongArray;
import org.mwg.struct.Buffer;
import org.mwg.struct.LMatrix;
import org.mwg.utility.Base64;

import java.util.Random;

class OffHeapLMatrix implements LMatrix {

    private static final int INDEX_ROWS = 0;
    private static final int INDEX_COLUMNS = 1;
    private static final int INDEX_MAX_COLUMN = 2;
    private static final int INDEX_OFFSET = 3;
    private final long index;
    private final OffHeapStateChunk chunk;

    OffHeapLMatrix(final OffHeapStateChunk p_chunk, final long p_index) {
        chunk = p_chunk;
        index = p_index;
    }

    @Override
    public final LMatrix init(int rows, int columns) {
        chunk.lock();
        try {
            long addr = chunk.addrByIndex(index);
            if (addr != OffHeapConstants.OFFHEAP_NULL_PTR) {
                OffHeapLongArray.free(addr);
            }
            addr = OffHeapLongArray.allocate(rows * columns + INDEX_OFFSET);
            OffHeapLongArray.set(addr, INDEX_ROWS, rows);
            OffHeapLongArray.set(addr, INDEX_COLUMNS, columns);
            OffHeapLongArray.set(addr, INDEX_MAX_COLUMN, columns);
            chunk.setAddrByIndex(index, addr);
            chunk.declareDirty();
        } finally {
            chunk.unlock();
        }
        return this;
    }

    @Override
    public LMatrix appendColumn(long[] newColumn) {
        chunk.lock();
        try {
            long nbRows;
            long nbColumns;
            long nbMaxColumn;
            long addr = chunk.addrByIndex(index);
            if (addr == OffHeapConstants.OFFHEAP_NULL_PTR) {
                nbRows = newColumn.length;
                nbColumns = Constants.MAP_INITIAL_CAPACITY;
                nbMaxColumn = 0;
                addr = OffHeapLongArray.allocate(nbRows * nbColumns + INDEX_OFFSET);
                OffHeapLongArray.set(addr, INDEX_ROWS, nbRows);
                OffHeapLongArray.set(addr, INDEX_COLUMNS, nbColumns);
                OffHeapLongArray.set(addr, INDEX_MAX_COLUMN, nbMaxColumn);
                chunk.setAddrByIndex(index, addr);
                chunk.declareDirty();
            } else {
                nbRows = (int) OffHeapLongArray.get(addr, INDEX_ROWS);
                nbColumns = (int) OffHeapLongArray.get(addr, INDEX_COLUMNS);
                nbMaxColumn = (int) OffHeapLongArray.get(addr, INDEX_MAX_COLUMN);
            }
            if (nbMaxColumn == nbColumns) {
                nbColumns = nbColumns * 2;
                final long newLength = nbColumns * nbRows + INDEX_OFFSET;
                addr = OffHeapLongArray.reallocate(addr, newLength);
                chunk.setAddrByIndex(index, addr);
                chunk.declareDirty();
            }
            for (int i = 0; i < newColumn.length; i++) {
                long base = nbMaxColumn * nbRows + INDEX_OFFSET;
                OffHeapLongArray.set(addr, i + base, newColumn[i]);
            }
            OffHeapLongArray.set(addr, INDEX_MAX_COLUMN, nbMaxColumn + 1);
        } finally {
            chunk.unlock();
        }
        return this;
    }

    void unsafe_init(int size) {
        long addr = chunk.addrByIndex(index);
        if (addr != OffHeapConstants.OFFHEAP_NULL_PTR) {
            OffHeapLongArray.free(addr);
        }
        addr = OffHeapLongArray.allocate(size);
        OffHeapLongArray.set(addr, INDEX_ROWS, 0);
        OffHeapLongArray.set(addr, INDEX_COLUMNS, 0);
        chunk.setAddrByIndex(index, addr);
    }

    void unsafe_set(long setIndex, long value) {
        final long addr = chunk.addrByIndex(index);
        if (addr != OffHeapConstants.OFFHEAP_NULL_PTR) {
            OffHeapLongArray.set(addr, setIndex, value);
        }
    }

    @Override
    public final LMatrix fill(long value) {
        chunk.lock();
        try {
            final long addr = chunk.addrByIndex(index);
            if (addr != OffHeapConstants.OFFHEAP_NULL_PTR) {
                long nbRows = OffHeapLongArray.get(addr, INDEX_ROWS);
                long nbColumns = OffHeapLongArray.get(addr, INDEX_COLUMNS);
                OffHeapLongArray.fill(addr, INDEX_OFFSET, (nbRows * nbColumns), value);
                chunk.declareDirty();
            }
        } finally {
            chunk.unlock();
        }
        return this;
    }

    @Override
    public LMatrix fillWith(long[] values) {
        chunk.lock();
        try {
            final long addr = chunk.addrByIndex(index);
            if (addr != OffHeapConstants.OFFHEAP_NULL_PTR) {
                for (int i = 0; i < values.length; i++) {
                    OffHeapLongArray.set(addr, INDEX_OFFSET + i, values[i]);
                }
                chunk.declareDirty();
            }
        } finally {
            chunk.unlock();
        }
        return this;
    }

    @Override
    public LMatrix fillWithRandom(long min, long max, long seed) {
        chunk.lock();
        try {
            final long addr = chunk.addrByIndex(index);
            if (addr != OffHeapConstants.OFFHEAP_NULL_PTR) {
                final long nbRows = OffHeapLongArray.get(addr, INDEX_ROWS);
                final long nbColumns = OffHeapLongArray.get(addr, INDEX_COLUMNS);
                final Random rand = new Random();
                rand.setSeed(seed);
                for (int i = 0; i < nbColumns * nbRows; i++) {
                    OffHeapLongArray.set(addr, INDEX_OFFSET + i, rand.nextLong() * (max - min) + min);
                }
                chunk.declareDirty();
            }
        } finally {
            chunk.unlock();
        }
        return this;
    }

    @Override
    public final int rows() {
        chunk.lock();
        int result = 0;
        try {
            final long addr = chunk.addrByIndex(index);
            if (addr != OffHeapConstants.OFFHEAP_NULL_PTR) {
                result = (int) OffHeapLongArray.get(addr, INDEX_ROWS);
            }
        } finally {
            chunk.unlock();
        }
        return result;
    }

    @Override
    public final int columns() {
        chunk.lock();
        int result = 0;
        try {
            final long addr = chunk.addrByIndex(index);
            if (addr != OffHeapConstants.OFFHEAP_NULL_PTR) {
                result = (int) OffHeapLongArray.get(addr, INDEX_MAX_COLUMN);
            }
        } finally {
            chunk.unlock();
        }
        return result;
    }

    @Override
    public long[] column(int columnIndex) {
        long[] result = null;
        chunk.lock();
        try {
            final long addr = chunk.addrByIndex(index);
            if (addr != OffHeapConstants.OFFHEAP_NULL_PTR) {
                long nbRows = OffHeapLongArray.get(addr, INDEX_ROWS);
                result = new long[(int) nbRows];
                long base = INDEX_OFFSET + (columnIndex * nbRows);
                for (int i = 0; i < nbRows; i++) {
                    result[i] = OffHeapLongArray.get(addr, base + i);
                }
            }
        } finally {
            chunk.unlock();
        }
        return result;
    }

    @Override
    public final long get(int rowIndex, int columnIndex) {
        chunk.lock();
        long result = 0;
        try {
            final long addr = chunk.addrByIndex(index);
            if (addr != OffHeapConstants.OFFHEAP_NULL_PTR) {
                final long nbRows = (int) OffHeapLongArray.get(addr, INDEX_ROWS);
                result = OffHeapLongArray.get(addr, INDEX_OFFSET + rowIndex + columnIndex * nbRows);
            }
        } finally {
            chunk.unlock();
        }
        return result;
    }

    @Override
    public final LMatrix set(int rowIndex, int columnIndex, long value) {
        chunk.lock();
        try {
            final long addr = chunk.addrByIndex(index);
            if (addr != OffHeapConstants.OFFHEAP_NULL_PTR) {
                final long nbRows = OffHeapLongArray.get(addr, INDEX_ROWS);
                OffHeapLongArray.set(addr, INDEX_OFFSET + rowIndex + columnIndex * nbRows, value);
            }
        } finally {
            chunk.unlock();
        }
        return this;
    }

    @Override
    public LMatrix add(int rowIndex, int columnIndex, long value) {
        chunk.lock();
        try {
            final long addr = chunk.addrByIndex(index);
            if (addr != OffHeapConstants.OFFHEAP_NULL_PTR) {
                final long nbRows = OffHeapLongArray.get(addr, INDEX_ROWS);
                final long raw_index = INDEX_OFFSET + rowIndex + columnIndex * nbRows;
                final long previous = OffHeapLongArray.get(addr, raw_index);
                OffHeapLongArray.set(addr, raw_index, value + previous);
            }
        } finally {
            chunk.unlock();
        }
        return this;
    }

    @Override
    public long[] data() {
        chunk.lock();
        long[] flat;
        try {
            final long addr = chunk.addrByIndex(index);
            if (addr != OffHeapConstants.OFFHEAP_NULL_PTR) {
                final int nbRows = (int) OffHeapLongArray.get(addr, INDEX_ROWS);
                final int nbColumns = (int) OffHeapLongArray.get(addr, INDEX_COLUMNS);
                final int flatSize = nbRows * nbColumns;
                flat = new long[flatSize];
                for (int i = 0; i < flatSize; i++) {
                    flat[i] = OffHeapLongArray.get(addr, i + INDEX_OFFSET);
                }
            } else {
                flat = new long[0];
            }
        } finally {
            chunk.unlock();
        }
        return flat;
    }

    @Override
    public int leadingDimension() {
        chunk.lock();
        int result = 0;
        try {
            final long addr = chunk.addrByIndex(index);
            if (addr != OffHeapConstants.OFFHEAP_NULL_PTR) {
                final int nbRows = (int) OffHeapLongArray.get(addr, INDEX_ROWS);
                final int nbColumns = (int) OffHeapLongArray.get(addr, INDEX_COLUMNS);
                result = Math.max(nbRows, nbColumns);
            }
        } finally {
            chunk.unlock();
        }
        return result;
    }

    @Override
    public long unsafeGet(int indexValue) {
        chunk.lock();
        long result = 0;
        try {
            final long addr = chunk.addrByIndex(index);
            if (addr != OffHeapConstants.OFFHEAP_NULL_PTR) {
                result = OffHeapLongArray.get(addr, INDEX_OFFSET + indexValue);
            }
        } finally {
            chunk.unlock();
        }
        return result;
    }

    @Override
    public LMatrix unsafeSet(int indexValue, long value) {
        chunk.lock();
        try {
            final long addr = chunk.addrByIndex(index);
            if (addr != OffHeapConstants.OFFHEAP_NULL_PTR) {
                OffHeapLongArray.set(addr, INDEX_OFFSET + indexValue, value);
            }
        } finally {
            chunk.unlock();
        }
        return this;
    }

    /*
    @Override
    public final String toString() {
        StringBuilder buffer = new StringBuilder();
        //chunk.lock();
        // try {
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
        //} finally {
        // chunk.unlock();
        //}
        return buffer.toString();
    }*/

    static void save(final long addr, final Buffer buffer) {
        if (addr == OffHeapConstants.OFFHEAP_NULL_PTR) {
            return;
        }
        final long nbRows = OffHeapLongArray.get(addr, INDEX_ROWS);
        final long nbColumns = OffHeapLongArray.get(addr, INDEX_COLUMNS);
        final long flatSize = nbRows * nbColumns;
        final long size = flatSize + INDEX_OFFSET;
        Base64.encodeLongToBuffer(size, buffer);
        for (long i = 0; i < size; i++) {
            buffer.write(Constants.CHUNK_VAL_SEP);
            Base64.encodeDoubleToBuffer(OffHeapLongArray.get(addr, i), buffer);
        }
    }

    static long clone(final long addr) {
        if (addr == OffHeapConstants.OFFHEAP_NULL_PTR) {
            return OffHeapConstants.OFFHEAP_NULL_PTR;
        }
        final long nbRows = (int) OffHeapLongArray.get(addr, INDEX_ROWS);
        final long nbColumns = (int) OffHeapLongArray.get(addr, INDEX_COLUMNS);
        final long flatSize = nbRows * nbColumns;
        return OffHeapLongArray.cloneArray(addr, flatSize + INDEX_OFFSET);
    }

    static void free(final long addr) {
        OffHeapLongArray.free(addr);
    }

    final long load(final Buffer buffer, final long offset, final long max) {
        long cursor = offset;
        byte current = buffer.read(cursor);
        boolean isFirst = true;
        long previous = offset;
        long elemIndex = 0;
        while (cursor < max && current != Constants.CHUNK_SEP && current != Constants.CHUNK_ENODE_SEP) {
            if (current == Constants.CHUNK_VAL_SEP) {
                if (isFirst) {
                    unsafe_init(Base64.decodeToIntWithBounds(buffer, previous, cursor));
                    isFirst = false;
                } else {
                    unsafe_set(elemIndex, Base64.decodeToLongWithBounds(buffer, previous, cursor));
                    elemIndex++;
                }
                previous = cursor + 1;
            }
            cursor++;
            current = buffer.read(cursor);
        }
        if (isFirst) {
            unsafe_init((int) Base64.decodeToLongWithBounds(buffer, previous, cursor));
        } else {
            unsafe_set(elemIndex, Base64.decodeToLongWithBounds(buffer, previous, cursor));
        }
        return cursor;
    }

}
