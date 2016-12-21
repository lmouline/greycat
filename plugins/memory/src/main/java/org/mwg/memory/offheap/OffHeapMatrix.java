package org.mwg.memory.offheap;

import org.mwg.Constants;
import org.mwg.memory.offheap.primary.OffHeapDoubleArray;
import org.mwg.memory.offheap.primary.OffHeapLongArray;
import org.mwg.struct.Buffer;
import org.mwg.struct.Matrix;
import org.mwg.utility.Base64;

import java.util.Random;

class OffHeapMatrix implements Matrix {

    private static final int INDEX_ROWS = 0;
    private static final int INDEX_COLUMNS = 1;
    private static final int INDEX_MAX_COLUMN = 2;
    private static final int INDEX_OFFSET = 3;
    private final long index;
    private final OffHeapStateChunk chunk;

    OffHeapMatrix(final OffHeapStateChunk p_chunk, final long p_index) {
        chunk = p_chunk;
        index = p_index;
    }

    @Override
    public final Matrix init(int rows, int columns) {
        chunk.lock();
        try {
            long addr = chunk.addrByIndex(index);
            if (addr != OffHeapConstants.OFFHEAP_NULL_PTR) {
                OffHeapDoubleArray.free(addr);
            }
            addr = OffHeapDoubleArray.allocate(rows * columns + INDEX_OFFSET);
            OffHeapDoubleArray.set(addr, INDEX_ROWS, rows);
            OffHeapDoubleArray.set(addr, INDEX_COLUMNS, columns);
            OffHeapDoubleArray.set(addr, INDEX_MAX_COLUMN, columns);
            chunk.setAddrByIndex(index, addr);
            chunk.declareDirty();
        } finally {
            chunk.unlock();
        }
        return this;
    }

    @Override
    public Matrix appendColumn(double[] newColumn) {
        chunk.lock();
        try {
            int nbRows;
            int nbColumns;
            int nbMaxColumn;
            long addr = chunk.addrByIndex(index);
            if (addr == OffHeapConstants.OFFHEAP_NULL_PTR) {
                nbRows = newColumn.length;
                nbColumns = Constants.MAP_INITIAL_CAPACITY;
                nbMaxColumn = 0;
                addr = OffHeapDoubleArray.allocate(nbRows * nbColumns + INDEX_OFFSET);
                OffHeapDoubleArray.set(addr, INDEX_ROWS, nbRows);
                OffHeapDoubleArray.set(addr, INDEX_COLUMNS, nbColumns);
                OffHeapDoubleArray.set(addr, INDEX_MAX_COLUMN, nbMaxColumn);
                chunk.setAddrByIndex(index, addr);
                chunk.declareDirty();
            } else {
                nbRows = (int) OffHeapDoubleArray.get(addr, INDEX_ROWS);
                nbColumns = (int) OffHeapDoubleArray.get(addr, INDEX_COLUMNS);
                nbMaxColumn = (int) OffHeapDoubleArray.get(addr, INDEX_MAX_COLUMN);
            }
            if (nbMaxColumn == nbColumns) {
                nbColumns = nbColumns * 2;
                final int newLength = nbColumns * nbRows + INDEX_OFFSET;
                addr = OffHeapDoubleArray.reallocate(addr, newLength);
                chunk.setAddrByIndex(index, addr);
                chunk.declareDirty();
            }
            for (int i = 0; i < newColumn.length; i++) {
                long base = nbMaxColumn * nbRows + INDEX_OFFSET;
                OffHeapDoubleArray.set(addr, i + base, newColumn[i]);
            }
            OffHeapDoubleArray.set(addr, INDEX_MAX_COLUMN, nbMaxColumn + 1);
        } finally {
            chunk.unlock();
        }
        return this;
    }

    void unsafe_init(int size) {
        long addr = chunk.addrByIndex(index);
        if (addr != OffHeapConstants.OFFHEAP_NULL_PTR) {
            OffHeapDoubleArray.free(addr);
        }
        addr = OffHeapDoubleArray.allocate(size);
        OffHeapDoubleArray.set(addr, INDEX_ROWS, 0);
        OffHeapDoubleArray.set(addr, INDEX_COLUMNS, 0);
        chunk.setAddrByIndex(index, addr);
    }

    void unsafe_set(long setIndex, double value) {
        final long addr = chunk.addrByIndex(index);
        if (addr != OffHeapConstants.OFFHEAP_NULL_PTR) {
            OffHeapDoubleArray.set(addr, setIndex, value);
        }
    }

    @Override
    public final Matrix fill(double value) {
        chunk.lock();
        try {
            final long addr = chunk.addrByIndex(index);
            if (addr != OffHeapConstants.OFFHEAP_NULL_PTR) {
                int nbRows = (int) OffHeapDoubleArray.get(addr, INDEX_ROWS);
                int nbColumns = (int) OffHeapDoubleArray.get(addr, INDEX_COLUMNS);
                OffHeapDoubleArray.fill(addr, INDEX_OFFSET, (nbRows * nbColumns), value);
                chunk.declareDirty();
            }
        } finally {
            chunk.unlock();
        }
        return this;
    }

    @Override
    public Matrix fillWith(double[] values) {
        chunk.lock();
        try {
            final long addr = chunk.addrByIndex(index);
            if (addr != OffHeapConstants.OFFHEAP_NULL_PTR) {
                for (int i = 0; i < values.length; i++) {
                    OffHeapDoubleArray.set(addr, INDEX_OFFSET + i, values[i]);
                }
                chunk.declareDirty();
            }
        } finally {
            chunk.unlock();
        }
        return this;
    }

    @Override
    public Matrix fillWithRandom(double min, double max, long seed) {
        chunk.lock();
        try {
            final long addr = chunk.addrByIndex(index);
            if (addr != OffHeapConstants.OFFHEAP_NULL_PTR) {
                final int nbRows = (int) OffHeapDoubleArray.get(addr, INDEX_ROWS);
                final int nbColumns = (int) OffHeapDoubleArray.get(addr, INDEX_COLUMNS);
                final Random rand = new Random();
                rand.setSeed(seed);
                for (int i = 0; i < nbColumns * nbRows; i++) {
                    OffHeapDoubleArray.set(addr, INDEX_OFFSET + i, rand.nextDouble() * (max - min) + min);
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
                result = (int) OffHeapDoubleArray.get(addr, INDEX_ROWS);
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
                result = (int) OffHeapDoubleArray.get(addr, INDEX_MAX_COLUMN);
            }
        } finally {
            chunk.unlock();
        }
        return result;
    }

    @Override
    public double[] column(int columnIndex) {
        double[] result = null;
        chunk.lock();
        try {
            final long addr = chunk.addrByIndex(index);
            if (addr != OffHeapConstants.OFFHEAP_NULL_PTR) {
                long nbRows = (int) OffHeapDoubleArray.get(addr, INDEX_ROWS);
                result = new double[(int) nbRows];
                long base = INDEX_OFFSET + (columnIndex * nbRows);
                for (int i = 0; i < nbRows; i++) {
                    result[i] = OffHeapDoubleArray.get(addr, base + i);
                }
            }
        } finally {
            chunk.unlock();
        }
        return result;
    }

    @Override
    public final double get(int rowIndex, int columnIndex) {
        chunk.lock();
        double result = 0;
        try {
            final long addr = chunk.addrByIndex(index);
            if (addr != OffHeapConstants.OFFHEAP_NULL_PTR) {
                final int nbRows = (int) OffHeapDoubleArray.get(addr, INDEX_ROWS);
                result = OffHeapDoubleArray.get(addr, INDEX_OFFSET + rowIndex + columnIndex * nbRows);
            }
        } finally {
            chunk.unlock();
        }
        return result;
    }

    @Override
    public final Matrix set(int rowIndex, int columnIndex, double value) {
        chunk.lock();
        try {
            final long addr = chunk.addrByIndex(index);
            if (addr != OffHeapConstants.OFFHEAP_NULL_PTR) {
                final int nbRows = (int) OffHeapDoubleArray.get(addr, INDEX_ROWS);
                OffHeapDoubleArray.set(addr, INDEX_OFFSET + rowIndex + columnIndex * nbRows, value);
            }
        } finally {
            chunk.unlock();
        }
        return this;
    }

    @Override
    public Matrix add(int rowIndex, int columnIndex, double value) {
        chunk.lock();
        try {
            final long addr = chunk.addrByIndex(index);
            if (addr != OffHeapConstants.OFFHEAP_NULL_PTR) {
                final int nbRows = (int) OffHeapDoubleArray.get(addr, INDEX_ROWS);
                final int raw_index = INDEX_OFFSET + rowIndex + columnIndex * nbRows;
                final double previous = OffHeapDoubleArray.get(addr, raw_index);
                OffHeapDoubleArray.set(addr, raw_index, value + previous);
            }
        } finally {
            chunk.unlock();
        }
        return this;
    }

    @Override
    public double[] data() {
        chunk.lock();
        double[] flat;
        try {
            final long addr = chunk.addrByIndex(index);
            if (addr != OffHeapConstants.OFFHEAP_NULL_PTR) {
                final int nbRows = (int) OffHeapDoubleArray.get(addr, INDEX_ROWS);
                final int nbColumns = (int) OffHeapDoubleArray.get(addr, INDEX_COLUMNS);
                final int flatSize = nbRows * nbColumns;
                flat = new double[flatSize];
                for (int i = 0; i < flatSize; i++) {
                    flat[i] = OffHeapDoubleArray.get(addr, i + INDEX_OFFSET);
                }
            } else {
                flat = new double[0];
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
                final int nbRows = (int) OffHeapDoubleArray.get(addr, INDEX_ROWS);
                final int nbColumns = (int) OffHeapDoubleArray.get(addr, INDEX_COLUMNS);
                result = Math.max(nbRows, nbColumns);
            }
        } finally {
            chunk.unlock();
        }
        return result;
    }

    @Override
    public double unsafeGet(int indexValue) {
        chunk.lock();
        double result = 0;
        try {
            final long addr = chunk.addrByIndex(index);
            if (addr != OffHeapConstants.OFFHEAP_NULL_PTR) {
                result = OffHeapDoubleArray.get(addr, INDEX_OFFSET + indexValue);
            }
        } finally {
            chunk.unlock();
        }
        return result;
    }

    @Override
    public Matrix unsafeSet(int indexValue, double value) {
        chunk.lock();
        try {
            final long addr = chunk.addrByIndex(index);
            if (addr != OffHeapConstants.OFFHEAP_NULL_PTR) {
                OffHeapDoubleArray.set(addr, INDEX_OFFSET + indexValue, value);
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
        final int nbRows = (int) OffHeapDoubleArray.get(addr, INDEX_ROWS);
        final int nbColumns = (int) OffHeapDoubleArray.get(addr, INDEX_COLUMNS);
        final int flatSize = nbRows * nbColumns;
        final int size = flatSize + INDEX_OFFSET;
        Base64.encodeLongToBuffer(size, buffer);
        for (long i = 0; i < size; i++) {
            buffer.write(Constants.CHUNK_SUB_SUB_SEP);
            Base64.encodeDoubleToBuffer(OffHeapDoubleArray.get(addr, i), buffer);
        }
    }

    static long clone(final long addr) {
        if (addr == OffHeapConstants.OFFHEAP_NULL_PTR) {
            return OffHeapConstants.OFFHEAP_NULL_PTR;
        }
        final int nbRows = (int) OffHeapDoubleArray.get(addr, INDEX_ROWS);
        final int nbColumns = (int) OffHeapDoubleArray.get(addr, INDEX_COLUMNS);
        final int flatSize = nbRows * nbColumns;
        return OffHeapLongArray.cloneArray(addr, flatSize + INDEX_OFFSET);
    }

    static void free(final long addr) {
        OffHeapDoubleArray.free(addr);
    }

}
