package org.mwg.core.chunk.heap;

import org.mwg.Constants;
import org.mwg.struct.LMatrix;

import java.util.Arrays;
import java.util.Random;

class HeapLMatrix implements LMatrix {

    private static final int INDEX_ROWS = 0;
    private static final int INDEX_COLUMNS = 1;
    private static final int INDEX_MAX_COLUMN = 2;
    private static final int INDEX_OFFSET = 3;

    private final HeapStateChunk parent;
    private long[] backend = null;
    private boolean aligned = true;

    HeapLMatrix(final HeapStateChunk p_parent, final HeapLMatrix origin) {
        parent = p_parent;
        if (origin != null) {
            aligned = false;
            backend = origin.backend;
        }
    }

    @Override
    public final LMatrix init(final int rows, final int columns) {
        if (parent != null) {
            synchronized (parent) {
                internal_init(rows, columns);
            }
            parent.declareDirty();
        } else {
            internal_init(rows, columns);
        }
        return this;
    }

    private void internal_init(final int rows, final int columns) {
        //clean backend for OffHeap version
        backend = new long[rows * columns + INDEX_OFFSET];
        backend[INDEX_ROWS] = rows;
        backend[INDEX_COLUMNS] = columns;
        backend[INDEX_MAX_COLUMN] = columns;//direct allocation
        aligned = true;
    }

    public final LMatrix appendColumn(long[] newColumn) {
        if (parent != null) {
            synchronized (parent) {
                internal_appendColumn(newColumn);
                parent.declareDirty();
            }
        } else {
            internal_appendColumn(newColumn);
        }
        return this;
    }

    private void internal_appendColumn(long[] newColumn) {
        int nbRows;
        int nbColumns;
        int nbMaxColumn;
        if (backend == null) {
            nbRows = newColumn.length;
            nbColumns = Constants.MAP_INITIAL_CAPACITY;
            nbMaxColumn = 0;
            backend = new long[nbRows * nbColumns + INDEX_OFFSET];
            backend[INDEX_ROWS] = nbRows;
            backend[INDEX_COLUMNS] = nbColumns;
            backend[INDEX_MAX_COLUMN] = nbMaxColumn;
        } else {
            nbColumns = (int) backend[INDEX_COLUMNS];
            nbRows = (int) backend[INDEX_ROWS];
            nbMaxColumn = (int) backend[INDEX_MAX_COLUMN];
        }
        if (!aligned || nbMaxColumn == nbColumns) {
            if (nbMaxColumn == nbColumns) {
                nbColumns = nbColumns * 2;
                final int newLength = nbColumns * nbRows + INDEX_OFFSET;
                long[] next_backend = new long[newLength];
                System.arraycopy(backend, 0, next_backend, 0, backend.length);
                backend = next_backend;
                aligned = true;
            } else {
                //direct copy
                long[] next_backend = new long[backend.length];
                System.arraycopy(backend, 0, next_backend, 0, backend.length);
                backend = next_backend;
                aligned = true;
            }
        }
        //just insert
        System.arraycopy(newColumn, 0, backend, (nbMaxColumn * nbRows) + INDEX_OFFSET, newColumn.length);
        backend[INDEX_MAX_COLUMN] = nbMaxColumn + 1;
    }

    @Override
    public final LMatrix fill(long value) {
        synchronized (parent) {
            if (backend != null) {
                if (!aligned) {
                    long[] next_backend = new long[backend.length];
                    System.arraycopy(backend, 0, next_backend, 0, backend.length);
                    backend = next_backend;
                    aligned = true;
                }
                Arrays.fill(backend, INDEX_OFFSET, backend.length - INDEX_OFFSET, value);
                parent.declareDirty();
                backend[INDEX_MAX_COLUMN] = backend[INDEX_COLUMNS];
            }
        }
        return this;
    }

    @Override
    public LMatrix fillWith(long[] values) {
        synchronized (parent) {
            if (backend != null) {
                if (!aligned) {
                    long[] next_backend = new long[backend.length];
                    System.arraycopy(backend, 0, next_backend, 0, backend.length);
                    backend = next_backend;
                    aligned = true;
                }
                //reInit ?
                System.arraycopy(values, 0, backend, INDEX_OFFSET, values.length);
                parent.declareDirty();
            }
        }
        return this;
    }

    @Override
    public LMatrix fillWithRandom(long min, long max, long seed) {
        synchronized (parent) {
            Random rand = new Random();
            rand.setSeed(seed);
            if (backend != null) {
                if (!aligned) {
                    long[] next_backend = new long[backend.length];
                    System.arraycopy(backend, 0, next_backend, 0, backend.length);
                    backend = next_backend;
                    aligned = true;
                }
                for (int i = 0; i < backend[INDEX_ROWS] * backend[INDEX_COLUMNS]; i++) {
                    backend[i + INDEX_OFFSET] = rand.nextInt() * (max - min) + min;
                }
                parent.declareDirty();
            }
        }
        return this;
    }

    @Override
    public final int rows() {
        int result = 0;
        synchronized (parent) {
            if (backend != null) {
                result = (int) backend[INDEX_ROWS];
            }
        }
        return result;
    }

    @Override
    public final int columns() {
        int result = 0;
        synchronized (parent) {
            if (backend != null) {
                result = (int) backend[INDEX_MAX_COLUMN];
            }
        }
        return result;
    }

    @Override
    public final long[] column(int index) {
        long[] result;
        synchronized (parent) {
            final int nbRows = (int) backend[INDEX_ROWS];
            result = new long[nbRows];
            System.arraycopy(backend, INDEX_OFFSET + (index * nbRows), result, 0, nbRows);
        }
        return result;
    }

    @Override
    public final long get(int rowIndex, int columnIndex) {
        long result = 0;
        synchronized (parent) {
            if (backend != null) {
                final int nbRows = (int) backend[INDEX_ROWS];
                result = backend[INDEX_OFFSET + rowIndex + columnIndex * nbRows];
            }
        }
        return result;
    }

    @Override
    public final LMatrix set(int rowIndex, int columnIndex, long value) {
        synchronized (parent) {
            if (backend != null) {
                if (!aligned) {
                    long[] next_backend = new long[backend.length];
                    System.arraycopy(backend, 0, next_backend, 0, backend.length);
                    backend = next_backend;
                    aligned = true;
                }
                final int nbRows = (int) backend[INDEX_ROWS];
                backend[INDEX_OFFSET + rowIndex + columnIndex * nbRows] = value;
                //update maxColumn field
                /*if (backend[columnIndex] > backend[INDEX_MAX_COLUMN]) {
                    backend[INDEX_COLUMNS] = columnIndex;
                }*/
                parent.declareDirty();
            }
        }
        return this;
    }

    @Override
    public LMatrix add(int rowIndex, int columnIndex, long value) {
        synchronized (parent) {
            if (backend != null) {
                if (!aligned) {
                    long[] next_backend = new long[backend.length];
                    System.arraycopy(backend, 0, next_backend, 0, backend.length);
                    backend = next_backend;
                    aligned = true;
                }
                final int nbRows = (int) backend[INDEX_ROWS];
                backend[INDEX_OFFSET + rowIndex + columnIndex * nbRows] = value + backend[INDEX_OFFSET + rowIndex + columnIndex * nbRows];
                //update maxColumn field
                /*if (backend[columnIndex] > backend[INDEX_MAX_COLUMN]) {
                    backend[INDEX_COLUMNS] = columnIndex;
                }*/
                parent.declareDirty();
            }
        }
        return this;
    }

    @Override
    public final long[] data() {
        long[] copy = null;
        synchronized (parent) {
            if (backend != null) {
                copy = new long[backend.length - INDEX_OFFSET];
                System.arraycopy(backend, INDEX_OFFSET, copy, 0, backend.length - INDEX_OFFSET);
            }
        }
        return copy;
    }

    @Override
    public int leadingDimension() {
        if (backend == null) {
            return 0;
        }
        return (int) Math.max(backend[INDEX_COLUMNS], backend[INDEX_ROWS]);
    }

    @Override
    public long unsafeGet(int index) {
        long result = 0;
        synchronized (parent) {
            if (backend != null) {
                result = backend[INDEX_OFFSET + index];
            }
        }
        return result;
    }

    @Override
    public LMatrix unsafeSet(int index, long value) {
        synchronized (parent) {
            if (backend != null) {
                if (!aligned) {
                    long[] next_backend = new long[backend.length];
                    System.arraycopy(backend, 0, next_backend, 0, backend.length);
                    backend = next_backend;
                    aligned = true;
                }
                backend[INDEX_OFFSET + index] = value;
                parent.declareDirty();
            }
        }
        return this;
    }

    long[] unsafe_data() {
        return backend;
    }

    void unsafe_init(long size) {
        backend = new long[(int) size];
        backend[INDEX_ROWS] = 0;
        backend[INDEX_COLUMNS] = 0;
        aligned = true;
    }

    void unsafe_set(long index, long value) {
        backend[(int) index] = value;
    }

}
