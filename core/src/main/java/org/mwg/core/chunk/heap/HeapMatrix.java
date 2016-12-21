package org.mwg.core.chunk.heap;

import org.mwg.Constants;
import org.mwg.struct.Matrix;

import java.util.Arrays;
import java.util.Random;

class HeapMatrix implements Matrix {

    private static final int INDEX_ROWS = 0;
    private static final int INDEX_COLUMNS = 1;
    private static final int INDEX_MAX_COLUMN = 2;
    private static final int INDEX_OFFSET = 3;

    private final HeapStateChunk parent;
    private double[] backend = null;
    private boolean aligned = true;

    HeapMatrix(final HeapStateChunk p_parent, final HeapMatrix origin) {
        parent = p_parent;
        if (origin != null) {
            aligned = false;
            backend = origin.backend;
        }
    }

    @Override
    public final Matrix init(int rows, int columns) {
        synchronized (parent) {
            //clean backend for OffHeap version
            backend = new double[rows * columns + INDEX_OFFSET];
            backend[INDEX_ROWS] = rows;
            backend[INDEX_COLUMNS] = columns;
            backend[INDEX_MAX_COLUMN] = columns;//direct allocation
            aligned = true;
            parent.declareDirty();
        }
        return this;
    }

    public final Matrix appendColumn(double[] newColumn) {
        synchronized (parent) {
            int nbRows;
            int nbColumns;
            int nbMaxColumn;
            if (backend == null) {
                nbRows = newColumn.length;
                nbColumns = Constants.MAP_INITIAL_CAPACITY;
                nbMaxColumn = 0;
                backend = new double[nbRows * nbColumns + INDEX_OFFSET];
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
                    double[] next_backend = new double[newLength];
                    System.arraycopy(backend, 0, next_backend, 0, backend.length);
                    backend = next_backend;
                    aligned = true;
                } else {
                    //direct copy
                    double[] next_backend = new double[backend.length];
                    System.arraycopy(backend, 0, next_backend, 0, backend.length);
                    backend = next_backend;
                    aligned = true;
                }
            }
            //just insert
            System.arraycopy(newColumn, 0, backend, (nbMaxColumn * nbRows) + INDEX_OFFSET, newColumn.length);
            backend[INDEX_MAX_COLUMN] = nbMaxColumn + 1;
            parent.declareDirty();
        }
        return this;
    }

    @Override
    public final Matrix fill(double value) {
        synchronized (parent) {
            if (backend != null) {
                if (!aligned) {
                    double[] next_backend = new double[backend.length];
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
    public Matrix fillWith(double[] values) {
        synchronized (parent) {
            if (backend != null) {
                if (!aligned) {
                    double[] next_backend = new double[backend.length];
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
    public Matrix fillWithRandom(double min, double max, long seed) {
        synchronized (parent) {
            Random rand = new Random();
            rand.setSeed(seed);
            if (backend != null) {
                if (!aligned) {
                    double[] next_backend = new double[backend.length];
                    System.arraycopy(backend, 0, next_backend, 0, backend.length);
                    backend = next_backend;
                    aligned = true;
                }
                for (int i = 0; i < backend[INDEX_ROWS] * backend[INDEX_COLUMNS]; i++) {
                    backend[i + INDEX_OFFSET] = rand.nextDouble() * (max - min) + min;
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
    public final double[] column(int index) {
        double[] result;
        synchronized (parent) {
            final int nbRows = (int) backend[INDEX_ROWS];
            result = new double[nbRows];
            System.arraycopy(backend, INDEX_OFFSET + (index * nbRows), result, 0, nbRows);
        }
        return result;
    }

    @Override
    public final double get(int rowIndex, int columnIndex) {
        double result = 0;
        synchronized (parent) {
            if (backend != null) {
                final int nbRows = (int) backend[INDEX_ROWS];
                result = backend[INDEX_OFFSET + rowIndex + columnIndex * nbRows];
            }
        }
        return result;
    }

    @Override
    public final Matrix set(int rowIndex, int columnIndex, double value) {
        synchronized (parent) {
            if (backend != null) {
                if (!aligned) {
                    double[] next_backend = new double[backend.length];
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
    public Matrix add(int rowIndex, int columnIndex, double value) {
        synchronized (parent) {
            if (backend != null) {
                if (!aligned) {
                    double[] next_backend = new double[backend.length];
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
    public final double[] data() {
        double[] copy = null;
        synchronized (parent) {
            if (backend != null) {
                copy = new double[backend.length - INDEX_OFFSET];
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
    public double unsafeGet(int index) {
        double result = 0;
        synchronized (parent) {
            if (backend != null) {
                result = backend[INDEX_OFFSET + index];
            }
        }
        return result;
    }

    @Override
    public Matrix unsafeSet(int index, double value) {
        synchronized (parent) {
            if (backend != null) {
                if (!aligned) {
                    double[] next_backend = new double[backend.length];
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

    double[] unsafe_data() {
        return backend;
    }

    void unsafe_init(long size) {
        backend = new double[(int) size];
        backend[INDEX_ROWS] = 0;
        backend[INDEX_COLUMNS] = 0;
        aligned = true;
    }

    void unsafe_set(long index, double value) {
        backend[(int) index] = value;
    }

}
