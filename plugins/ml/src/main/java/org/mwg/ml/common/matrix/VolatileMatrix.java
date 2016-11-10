package org.mwg.ml.common.matrix;


import org.mwg.struct.Matrix;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Random;

//Most of the time we will be using column based matrix due to blas.
public class VolatileMatrix implements Matrix {

    private double[] _data;
    private final int _nbRows;
    private final int _nbColumns;

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

    private static MatrixEngine _defaultEngine = null;

    /**
     * @native ts
     * if(VolatileMatrix._defaultEngine == null){
     * VolatileMatrix._defaultEngine = new org.mwg.ml.common.matrix.HybridMatrixEngine();
     * }
     * return VolatileMatrix._defaultEngine;
     */
    public static MatrixEngine defaultEngine() {
        if (_defaultEngine == null) {
            _defaultEngine = new HybridMatrixEngine();
        }
        return _defaultEngine;
    }

    public static void setDefaultEngine(MatrixEngine matrixEngine) {
        _defaultEngine = matrixEngine;
    }

    public static Matrix multiply(Matrix matA, Matrix matB) {
        return defaultEngine().multiplyTransposeAlphaBeta(TransposeType.NOTRANSPOSE, 1d, matA, TransposeType.NOTRANSPOSE, matB, 0, null);
    }

    public static Matrix multiplyTranspose(TransposeType transA, Matrix matA, TransposeType transB, Matrix matB) {
        return defaultEngine().multiplyTransposeAlphaBeta(transA, 1.0, matA, transB, matB, 0, null);
    }

    public static Matrix multiplyTransposeAlpha(TransposeType transA, double alpha, Matrix matA, TransposeType transB, Matrix matB) {
        return defaultEngine().multiplyTransposeAlphaBeta(transA, alpha, matA, transB, matB, 0, null);
    }

    public static Matrix multiplyTransposeAlphaBeta(TransposeType transA, double alpha, Matrix matA, TransposeType transB, Matrix matB, double beta, Matrix matC) {
        return defaultEngine().multiplyTransposeAlphaBeta(transA, alpha, matA, transB, matB, beta, matC);
    }

    public static Matrix invert(Matrix mat, boolean invertInPlace) {
        return defaultEngine().invert(mat, invertInPlace);
    }

    public static Matrix pinv(Matrix mat, boolean invertInPlace) {
        return defaultEngine().pinv(mat, invertInPlace);
    }

    public static Matrix random(int rows, int columns, double min, double max) {
        VolatileMatrix res = new VolatileMatrix(null, rows, columns);
        Random rand = new Random();
        for (int i = 0; i < rows * columns; i++) {
            res.unsafeSet(i, rand.nextDouble() * (max - min) + min);
        }
        return res;
    }

    public static void scale(double alpha, VolatileMatrix matA) {
        if (alpha == 0) {
            matA.fill(0);
            return;
        }
        for (int i = 0; i < matA.rows() * matA.columns(); i++) {
            matA.unsafeSet(i, alpha * matA.unsafeGet(i));
        }
    }

    public static Matrix transpose(Matrix matA) {
        Matrix result = new VolatileMatrix(null, matA.columns(), matA.rows());
        int TRANSPOSE_SWITCH = 375;
        if (matA.columns() == matA.rows()) {
            transposeSquare(matA, result);
        } else if (matA.columns() > TRANSPOSE_SWITCH && matA.rows() > TRANSPOSE_SWITCH) {
            transposeBlock(matA, result);
        } else {
            transposeStandard(matA, result);
        }
        return result;
    }

    private static void transposeSquare(Matrix matA, Matrix result) {
        int index = 1;
        int indexEnd = matA.columns();
        for (int i = 0; i < matA.rows(); i++) {
            int indexOther = (i + 1) * matA.columns() + i;
            int n = i * (matA.columns() + 1);
            result.unsafeSet(n, matA.unsafeGet(n));
            for (; index < indexEnd; index++) {
                result.unsafeSet(index, matA.unsafeGet(indexOther));
                result.unsafeSet(indexOther, matA.unsafeGet(index));
                indexOther += matA.columns();
            }
            index += i + 2;
            indexEnd += matA.columns();
        }
    }

    private static void transposeStandard(Matrix matA, Matrix result) {
        int index = 0;
        for (int i = 0; i < result.columns(); i++) {
            int index2 = i;
            int end = index + result.rows();
            while (index < end) {
                result.unsafeSet(index++, matA.unsafeGet(index2));
                index2 += matA.rows();
            }
        }
    }

    private static void transposeBlock(Matrix matA, Matrix result) {
        int BLOCK_WIDTH = 60;
        for (int j = 0; j < matA.columns(); j += BLOCK_WIDTH) {
            int blockWidth = Math.min(BLOCK_WIDTH, matA.columns() - j);
            int indexSrc = j * matA.rows();
            int indexDst = j;

            for (int i = 0; i < matA.rows(); i += BLOCK_WIDTH) {
                int blockHeight = Math.min(BLOCK_WIDTH, matA.rows() - i);
                int indexSrcEnd = indexSrc + blockHeight;

                for (; indexSrc < indexSrcEnd; indexSrc++) {
                    int colSrc = indexSrc;
                    int colDst = indexDst;
                    int end = colDst + blockWidth;
                    for (; colDst < end; colDst++) {
                        result.unsafeSet(colDst, matA.unsafeGet(colSrc));
                        colSrc += matA.rows();
                    }
                    indexDst += result.rows();
                }
            }
        }
    }

    public double[] saveToState() {
        double[] res = new double[_data.length + 2];
        res[0] = _nbRows;
        res[1] = _nbColumns;
        System.arraycopy(_data, 0, res, 2, _data.length);
        return res;
    }

    public static VolatileMatrix loadFromState(Object o) {
        double[] res = (double[]) o;
        double[] data = new double[res.length - 2];
        System.arraycopy(res, 2, data, 0, data.length);
        return new VolatileMatrix(data, (int) res[0], (int) res[1]);
    }

    public static VolatileMatrix createIdentity(int rows, int columns) {
        VolatileMatrix ret = new VolatileMatrix(null, rows, columns);
        int width = Math.min(rows, columns);
        for (int i = 0; i < width; i++) {
            ret.set(i, i, 1);
        }
        return ret;
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


    /**
     * @ignore ts
     */
    public static Matrix loadFromCsv(String csvfile) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(csvfile));
            String line;
            String[] data;
            int row = 0;
            int column = 0;

            while ((line = br.readLine()) != null) {
                data = line.split(",");
                column = data.length;
                row++;
            }
            Matrix X = new VolatileMatrix(null, row, column);
            int i = 0;
            br = new BufferedReader(new FileReader(csvfile));
            while ((line = br.readLine()) != null) {
                line = line.replace('"', ' ');
                data = line.split(",");
                int j = 0;
                for (String k : data) {
                    double d = Double.parseDouble(k);
                    X.set(i, j, d);
                    j++;
                }
                i++;
            }
            return X;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static boolean testDimensionsAB(TransposeType transA, TransposeType transB, Matrix matA, Matrix matB) {
        if (transA.equals(TransposeType.NOTRANSPOSE)) {
            if (transB.equals(TransposeType.NOTRANSPOSE)) {
                return (matA.columns() == matB.rows());
            } else {
                return (matA.columns() == matB.columns());
            }
        } else {
            if (transB.equals(TransposeType.NOTRANSPOSE)) {
                return (matA.rows() == matB.rows());
            } else {
                return (matA.rows() == matB.columns());
            }
        }
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
