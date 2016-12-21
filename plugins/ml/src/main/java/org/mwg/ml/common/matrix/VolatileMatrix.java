package org.mwg.ml.common.matrix;

import org.mwg.Constants;
import org.mwg.struct.Matrix;

import java.util.Random;

//Most of the time we will be using column based matrix due to blas.
public class VolatileMatrix implements Matrix {

    private double[] _data;
    private int _nbRows;
    private int _nbColumns;
    private int _nbMaxColumn;

    private VolatileMatrix(double[] backend, int p_nbRows, int p_nbColumns) {
        this._nbRows = p_nbRows;
        this._nbColumns = p_nbColumns;
        if (backend != null) {
            this._data = backend;
        } else {
            this._data = new double[_nbRows * _nbColumns];
        }
    }

    @Override
    public Matrix init(int rows, int columns) {
        if (rows != _nbRows && columns != _nbColumns) {
            throw new RuntimeException("Bad API usage !");
        }
        _nbMaxColumn = columns;
        this._data = new double[_nbRows * _nbColumns];
        return this;
    }

    @Override
    public double[] data() {
        //TODO copy array
        return _data;
    }

    @Override
    public int rows() {
        return _nbRows;
    }

    @Override
    public int columns() {
        return _nbColumns;
    }

    @Override
    public double[] column(int index) {
        double[] result = new double[_nbRows];
        System.arraycopy(_data, (index * _nbRows), result, 0, _nbRows);
        return result;
    }

    @Override
    public double get(int rowIndex, int columnIndex) {
        return _data[rowIndex + columnIndex * _nbRows];
    }

    @Override
    public Matrix set(int rowIndex, int columnIndex, double value) {
        _data[rowIndex + columnIndex * _nbRows] = value;
        return this;
    }

    @Override
    public Matrix add(int rowIndex, int columnIndex, double value) {
        int raw_index = rowIndex + columnIndex * _nbRows;
        _data[raw_index] = value + _data[raw_index];
        return this;
    }

    @Override
    public Matrix appendColumn(double[] newColumn) {
        if (_data == null) {
            _nbRows = newColumn.length;
            _nbColumns = Constants.MAP_INITIAL_CAPACITY;
            _nbMaxColumn = 0;
            _data = new double[_nbRows * _nbColumns];
        }
        if (_nbMaxColumn == _nbColumns) {
            _nbColumns = _nbColumns * 2;
            final int newLength = _nbColumns * _nbRows;
            double[] next_backend = new double[newLength];
            System.arraycopy(_data, 0, next_backend, 0, _data.length);
            _data = next_backend;
        }
        //just insert
        System.arraycopy(newColumn, 0, _data, _nbColumns * _nbRows, newColumn.length);
        _nbMaxColumn = _nbMaxColumn + 1;
        return null;
    }

    @Override
    public Matrix fill(double value) {
        for (int i = 0; i < _nbColumns * _nbRows; i++) {
            this._data[i] = value;
        }
        return this;
    }

    @Override
    public Matrix fillWith(double[] values) {
        _data = values;
        return this;
    }

    @Override
    public Matrix fillWithRandom(double min, double max, long seed) {
        Random rand = new Random();
        rand.setSeed(seed);
        for (int i = 0; i < _nbRows * _nbColumns; i++) {
            this._data[i] = rand.nextDouble() * (max - min) + min;
        }
        return this;
    }

    @Override
    public int leadingDimension() {
        return Math.max(_nbColumns, _nbRows);
    }

    @Override
    public double unsafeGet(int index) {
        return this._data[index];
    }

    @Override
    public Matrix unsafeSet(int index, double value) {
        this._data[index] = value;
        return this;
    }

    public static boolean compare(double[] a, double[] b, double eps) {
        if (a == null || b == null) {
            return false;
        }
        for (int i = 0; i < a.length; i++) {
            if (Math.abs(a[i] - b[i]) > eps) {
                return false;
            }
        }
        return true;
    }

    public static boolean compareArray(double[][] a, double[][] b, double eps) {
        if (a == null || b == null) {
            return false;
        }
        for (int i = 0; i < a.length; i++) {
            if (!compare(a[i], b[i], eps)) {
                return false;
            }
        }
        return true;
    }

    //   @Override
    public double[] exportRowMatrix() {
        double[] res = new double[_data.length];
        int k = 0;
        for (int i = 0; i < _nbRows; i++) {
            for (int j = 0; j < _nbColumns; j++) {
                res[k] = get(i, j);
                k++;
            }
        }
        return res;
    }

    //   @Override
    public VolatileMatrix importRowMatrix(double[] rowdata, int rows, int columns) {
        VolatileMatrix res = new VolatileMatrix(null, rows, columns);

        int k = 0;
        for (int i = 0; i < _nbRows; i++) {
            for (int j = 0; j < _nbColumns; j++) {
                res.set(i, j, rowdata[k]);
                k++;
            }
        }
        return res;
    }

    //   @Override
    // public void setData(double[] data) {
    // System.arraycopy(data, 0, this._data, 0, data.length);
    //  }

    //  @Override
    public double addAtIndex(int index, double value) {
        this._data[index] += value;
        return this._data[index];
    }

    //  @Override
    /*
    public VolatileMatrix clone() {
        double[] newback = new double[_data.length];
        System.arraycopy(_data, 0, newback, 0, _data.length);
        VolatileMatrix res = new VolatileMatrix(newback, this._nbRows, this._nbColumns);
        return res;

    }*/


    public static Matrix random(int rows, int columns, double min, double max) {
        VolatileMatrix res = new VolatileMatrix(null, rows, columns);
        Random rand = new Random();
        for (int i = 0; i < rows * columns; i++) {
            res.unsafeSet(i, rand.nextDouble() * (max - min) + min);
        }
        return res;
    }

    public static double compareMatrix(VolatileMatrix matA, VolatileMatrix matB) {
        double err = 0;

        for (int i = 0; i < matA.rows(); i++) {
            for (int j = 0; j < matA.columns(); j++) {
                if (err < Math.abs(matA.get(i, j) - matB.get(i, j))) {
                    err = Math.abs(matA.get(i, j) - matB.get(i, j));
                    // System.out.println(i+" , "+ j+" , "+ err);
                }

            }
        }
        return err;
    }


    public static Matrix identity(int rows, int columns) {
        Matrix res = new VolatileMatrix(null, rows, columns);
        for (int i = 0; i < Math.max(rows, columns); i++) {
            res.set(i, i, 1.0);
        }
        return res;
    }

    public static Matrix empty(int rows, int columns) {
        return new VolatileMatrix(null, rows, columns);
    }

    public static Matrix wrap(double[] data, int rows, int columns) {
        return new VolatileMatrix(data, rows, columns);
    }

    public static Matrix cloneFrom(Matrix origin) {
        //TODO checj according to .data() clone
        double[] prev = origin.data();
        double[] copy = new double[prev.length];
        System.arraycopy(prev, 0, copy, 0, copy.length);
        return new VolatileMatrix(copy, origin.rows(), origin.columns());
    }

}
