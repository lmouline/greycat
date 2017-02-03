package org.mwg.memory.offheap;

import org.mwg.Constants;
import org.mwg.memory.offheap.primary.OffHeapDoubleArray;
import org.mwg.memory.offheap.primary.OffHeapLongArray;
import org.mwg.struct.Buffer;
import org.mwg.struct.DMatrix;
import org.mwg.utility.Base64;

import java.util.Random;

class OffHeapDMatrix implements DMatrix {

    private static final int INDEX_ROWS = 0;
    private static final int INDEX_COLUMNS = 1;
    private static final int INDEX_MAX_COLUMN = 2;
    private static final int INDEX_OFFSET = 3;
    private final long index;
    private final OffHeapContainer container;

    OffHeapDMatrix(final OffHeapContainer p_container, final long p_index) {
        container = p_container;
        index = p_index;
    }

    @Override
    public final DMatrix init(int rows, int columns) {
        container.lock();
        try {
            long addr = container.addrByIndex(index);
            if (addr != OffHeapConstants.NULL_PTR) {
                OffHeapDoubleArray.free(addr);
            }
            addr = OffHeapDoubleArray.allocate(rows * columns + INDEX_OFFSET);
            OffHeapDoubleArray.set(addr, INDEX_ROWS, rows);
            OffHeapDoubleArray.set(addr, INDEX_COLUMNS, columns);
            OffHeapDoubleArray.set(addr, INDEX_MAX_COLUMN, columns);
            container.setAddrByIndex(index, addr);
            container.declareDirty();
        } finally {
            container.unlock();
        }
        return this;
    }

    @Override
    public DMatrix appendColumn(double[] newColumn) {
        container.lock();
        try {
            int nbRows;
            int nbColumns;
            int nbMaxColumn;
            long addr = container.addrByIndex(index);
            long indexAddr = OffHeapConstants.NULL_PTR;
            if (addr != OffHeapConstants.NULL_PTR) {
                indexAddr = OffHeapLongArray.get(addr, INDEX_COLUMNS);
            }
            if (addr == OffHeapConstants.NULL_PTR || indexAddr == OffHeapConstants.NULL_PTR) {
                nbRows = newColumn.length;
                nbColumns = Constants.MAP_INITIAL_CAPACITY;
                nbMaxColumn = 0;
                addr = OffHeapDoubleArray.allocate(nbRows * nbColumns + INDEX_OFFSET);
                OffHeapDoubleArray.set(addr, INDEX_ROWS, nbRows);
                OffHeapDoubleArray.set(addr, INDEX_COLUMNS, nbColumns);
                OffHeapDoubleArray.set(addr, INDEX_MAX_COLUMN, nbMaxColumn);
                container.setAddrByIndex(index, addr);
                container.declareDirty();
            } else {
                nbRows = (int) OffHeapDoubleArray.get(addr, INDEX_ROWS);
                nbColumns = (int) OffHeapDoubleArray.get(addr, INDEX_COLUMNS);
                nbMaxColumn = (int) OffHeapDoubleArray.get(addr, INDEX_MAX_COLUMN);
            }
            if (nbMaxColumn == nbColumns) {
                nbColumns = nbColumns * 2;
                final int newLength = nbColumns * nbRows + INDEX_OFFSET;
                addr = OffHeapDoubleArray.reallocate(addr, newLength);
                container.setAddrByIndex(index, addr);
                container.declareDirty();
            }
            for (int i = 0; i < newColumn.length; i++) {
                long base = nbMaxColumn * nbRows + INDEX_OFFSET;
                OffHeapDoubleArray.set(addr, i + base, newColumn[i]);
            }
            OffHeapDoubleArray.set(addr, INDEX_MAX_COLUMN, nbMaxColumn + 1);
        } finally {
            container.unlock();
        }
        return this;
    }

    void unsafe_init(int size) {
        long addr = container.addrByIndex(index);
        if (addr != OffHeapConstants.NULL_PTR) {
            OffHeapDoubleArray.free(addr);
        }
        addr = OffHeapDoubleArray.allocate(size);
        OffHeapDoubleArray.set(addr, INDEX_ROWS, 0);
        OffHeapDoubleArray.set(addr, INDEX_COLUMNS, 0);
        container.setAddrByIndex(index, addr);
    }

    void unsafe_set(long setIndex, double value) {
        final long addr = container.addrByIndex(index);
        if (addr != OffHeapConstants.NULL_PTR) {
            OffHeapDoubleArray.set(addr, setIndex, value);
        }
    }

    @Override
    public final DMatrix fill(double value) {
        container.lock();
        try {
            final long addr = container.addrByIndex(index);
            if (addr != OffHeapConstants.NULL_PTR) {
                int nbRows = (int) OffHeapDoubleArray.get(addr, INDEX_ROWS);
                int nbColumns = (int) OffHeapDoubleArray.get(addr, INDEX_COLUMNS);
                OffHeapDoubleArray.fill(addr, INDEX_OFFSET, (nbRows * nbColumns), value);
                container.declareDirty();
            }
        } finally {
            container.unlock();
        }
        return this;
    }

    @Override
    public DMatrix fillWith(double[] values) {
        container.lock();
        try {
            final long addr = container.addrByIndex(index);
            if (addr != OffHeapConstants.NULL_PTR) {
                for (int i = 0; i < values.length; i++) {
                    OffHeapDoubleArray.set(addr, INDEX_OFFSET + i, values[i]);
                }
                container.declareDirty();
            }
        } finally {
            container.unlock();
        }
        return this;
    }

    @Override
    public DMatrix fillWithRandom(Random random, double min, double max) {
        container.lock();
        try {
            final long addr = container.addrByIndex(index);
            if (addr != OffHeapConstants.NULL_PTR) {
                final int nbRows = (int) OffHeapDoubleArray.get(addr, INDEX_ROWS);
                final int nbColumns = (int) OffHeapDoubleArray.get(addr, INDEX_COLUMNS);
                   for (int i = 0; i < nbColumns * nbRows; i++) {
                    OffHeapDoubleArray.set(addr, INDEX_OFFSET + i, random.nextDouble() * (max - min) + min);
                }
                container.declareDirty();
            }
        } finally {
            container.unlock();
        }
        return this;
    }

    @Override
    public DMatrix fillWithRandomStd(Random random, double std) {
        container.lock();
        try {
            final long addr = container.addrByIndex(index);
            if (addr != OffHeapConstants.NULL_PTR) {
                final int nbRows = (int) OffHeapDoubleArray.get(addr, INDEX_ROWS);
                final int nbColumns = (int) OffHeapDoubleArray.get(addr, INDEX_COLUMNS);
                for (int i = 0; i < nbColumns * nbRows; i++) {
                    OffHeapDoubleArray.set(addr, INDEX_OFFSET + i, random.nextGaussian() * std);
                }
                container.declareDirty();
            }
        } finally {
            container.unlock();
        }
        return this;
    }



    @Override
    public final int rows() {
        container.lock();
        int result = 0;
        try {
            final long addr = container.addrByIndex(index);
            if (addr != OffHeapConstants.NULL_PTR) {
                result = (int) OffHeapDoubleArray.get(addr, INDEX_ROWS);
            }
        } finally {
            container.unlock();
        }
        return result;
    }

    @Override
    public final int columns() {
        container.lock();
        int result = 0;
        try {
            final long addr = container.addrByIndex(index);
            if (addr != OffHeapConstants.NULL_PTR) {
                result = (int) OffHeapDoubleArray.get(addr, INDEX_MAX_COLUMN);
            }
        } finally {
            container.unlock();
        }
        return result;
    }

    @Override
    public double[] column(int columnIndex) {
        double[] result = null;
        container.lock();
        try {
            final long addr = container.addrByIndex(index);
            if (addr != OffHeapConstants.NULL_PTR) {
                long nbRows = (int) OffHeapDoubleArray.get(addr, INDEX_ROWS);
                result = new double[(int) nbRows];
                long base = INDEX_OFFSET + (columnIndex * nbRows);
                for (int i = 0; i < nbRows; i++) {
                    result[i] = OffHeapDoubleArray.get(addr, base + i);
                }
            }
        } finally {
            container.unlock();
        }
        return result;
    }

    @Override
    public final double get(int rowIndex, int columnIndex) {
        container.lock();
        double result = 0;
        try {
            final long addr = container.addrByIndex(index);
            if (addr != OffHeapConstants.NULL_PTR) {
                final int nbRows = (int) OffHeapDoubleArray.get(addr, INDEX_ROWS);
                result = OffHeapDoubleArray.get(addr, INDEX_OFFSET + rowIndex + columnIndex * nbRows);
            }
        } finally {
            container.unlock();
        }
        return result;
    }

    @Override
    public final DMatrix set(int rowIndex, int columnIndex, double value) {
        container.lock();
        try {
            final long addr = container.addrByIndex(index);
            if (addr != OffHeapConstants.NULL_PTR) {
                final int nbRows = (int) OffHeapDoubleArray.get(addr, INDEX_ROWS);
                OffHeapDoubleArray.set(addr, INDEX_OFFSET + rowIndex + columnIndex * nbRows, value);
            }
        } finally {
            container.unlock();
        }
        return this;
    }

    @Override
    public DMatrix add(int rowIndex, int columnIndex, double value) {
        container.lock();
        try {
            final long addr = container.addrByIndex(index);
            if (addr != OffHeapConstants.NULL_PTR) {
                final int nbRows = (int) OffHeapDoubleArray.get(addr, INDEX_ROWS);
                final int raw_index = INDEX_OFFSET + rowIndex + columnIndex * nbRows;
                final double previous = OffHeapDoubleArray.get(addr, raw_index);
                OffHeapDoubleArray.set(addr, raw_index, value + previous);
            }
        } finally {
            container.unlock();
        }
        return this;
    }

    @Override
    public double[] data() {
        container.lock();
        double[] flat;
        try {
            final long addr = container.addrByIndex(index);
            if (addr != OffHeapConstants.NULL_PTR) {
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
            container.unlock();
        }
        return flat;
    }

    @Override
    public int leadingDimension() {
        container.lock();
        int result = 0;
        try {
            final long addr = container.addrByIndex(index);
            if (addr != OffHeapConstants.NULL_PTR) {
                final int nbRows = (int) OffHeapDoubleArray.get(addr, INDEX_ROWS);
                final int nbColumns = (int) OffHeapDoubleArray.get(addr, INDEX_COLUMNS);
                result = Math.max(nbRows, nbColumns);
            }
        } finally {
            container.unlock();
        }
        return result;
    }

    @Override
    public double unsafeGet(int indexValue) {
        container.lock();
        double result = 0;
        try {
            final long addr = container.addrByIndex(index);
            if (addr != OffHeapConstants.NULL_PTR) {
                result = OffHeapDoubleArray.get(addr, INDEX_OFFSET + indexValue);
            }
        } finally {
            container.unlock();
        }
        return result;
    }

    @Override
    public DMatrix unsafeSet(int indexValue, double value) {
        container.lock();
        try {
            final long addr = container.addrByIndex(index);
            if (addr != OffHeapConstants.NULL_PTR) {
                OffHeapDoubleArray.set(addr, INDEX_OFFSET + indexValue, value);
            }
        } finally {
            container.unlock();
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
        if (addr != OffHeapConstants.NULL_PTR) {
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
        if (addr == OffHeapConstants.NULL_PTR) {
            return;
        }
        final int nbRows = (int) OffHeapDoubleArray.get(addr, INDEX_ROWS);
        final int nbColumns = (int) OffHeapDoubleArray.get(addr, INDEX_COLUMNS);
        final int flatSize = nbRows * nbColumns;
        final int size = flatSize + INDEX_OFFSET;
        Base64.encodeLongToBuffer(size, buffer);
        for (long i = 0; i < size; i++) {
            buffer.write(Constants.CHUNK_VAL_SEP);
            Base64.encodeDoubleToBuffer(OffHeapDoubleArray.get(addr, i), buffer);
        }
    }

    static long clone(final long addr) {
        if (addr == OffHeapConstants.NULL_PTR) {
            return OffHeapConstants.NULL_PTR;
        }
        final int nbRows = (int) OffHeapDoubleArray.get(addr, INDEX_ROWS);
        final int nbColumns = (int) OffHeapDoubleArray.get(addr, INDEX_COLUMNS);
        final int flatSize = nbRows * nbColumns;
        return OffHeapLongArray.cloneArray(addr, flatSize + INDEX_OFFSET);
    }

    static void free(final long addr) {
        OffHeapDoubleArray.free(addr);
    }

    final long load(final Buffer buffer, final long offset, final long max) {
        long cursor = offset;
        byte current = buffer.read(cursor);
        boolean isFirst = true;
        long previous = offset;
        long elemIndex = 0;
        while (cursor < max && current != Constants.CHUNK_SEP && current != Constants.CHUNK_ENODE_SEP && current != Constants.CHUNK_ESEP) {
            if (current == Constants.CHUNK_VAL_SEP) {
                if (isFirst) {
                    unsafe_init(Base64.decodeToIntWithBounds(buffer, previous, cursor));
                    isFirst = false;
                } else {
                    unsafe_set(elemIndex, Base64.decodeToDoubleWithBounds(buffer, previous, cursor));
                    elemIndex++;
                }
                previous = cursor + 1;
            }
            cursor++;
            if (cursor < max) {
                current = buffer.read(cursor);
            }
        }
        if (isFirst) {
            unsafe_init((int) Base64.decodeToLongWithBounds(buffer, previous, cursor));
        } else {
            unsafe_set(elemIndex, Base64.decodeToDoubleWithBounds(buffer, previous, cursor));
        }
        return cursor;
    }


}
