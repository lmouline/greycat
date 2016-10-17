package org.mwg.ml.algorithm.preprocessing;

import org.mwg.ml.common.matrix.Matrix;
import org.mwg.ml.common.matrix.SVDDecompose;
import org.mwg.ml.common.matrix.TransposeType;

import java.text.DecimalFormat;

/**
 * Created by assaad on 27/09/16.
 */
public class PCA {
    private Matrix _data;
    private int _olddim;
    private double[] _min;
    private double[] _max;
    private double[] _sigma;
    private double[] _avg;
    private int _processType;
    private double _percentToRetain;

    SVDDecompose _svdDecompose;

    public static double EPS = 1e-30;

    private static Matrix _matrixV;

    public static int NOPROCESS = 0;
    public static int CENTER_ON_AVG = 1;
    public static int NORMALIZE = 2;


    private void normalizeData(Matrix data) {
        double d = 1;
        for (int j = 0; j < data.columns(); j++) {
            if (_sigma[j] < EPS) {
                for (int i = 0; i < data.rows(); i++) {
                    data.set(i, j, 0);
                }
            } else {
                d = 1 / _sigma[j];
                for (int i = 0; i < data.rows(); i++) {
                    data.set(i, j, (data.get(i, j) - _avg[j]) * d);
                }
            }
        }
    }

    public Matrix convertSpace(Matrix initial) {
        if (_processType == NORMALIZE) {
            normalizeData(initial);
        }
        Matrix res = Matrix.multiply(initial, _matrixV);
        return res;
    }


    public void setDimension(int dim) {
        _matrixV = new Matrix(null, _olddim, dim);
        Matrix tempV = _svdDecompose.getVt();
        for (int i = 0; i < _olddim; i++) {
            for (int j = 0; j < dim; j++) {
                _matrixV.set(i, j, tempV.get(j, i));
            }
        }
    }

    private void inverseNormalizeData(Matrix data) {
        for (int j = 0; j < data.columns(); j++) {
            if ((_sigma[j]) < EPS) {
                for (int i = 0; i < data.rows(); i++) {
                    data.set(i, j, _avg[j]);
                }
            } else {
                for (int i = 0; i < data.rows(); i++) {
                    data.set(i, j, data.get(i, j) * _sigma[j] + _avg[j]);
                }
            }
        }
    }

    public Matrix inverseConvertSpace(Matrix initial) {
        Matrix res = Matrix.multiplyTranspose(TransposeType.NOTRANSPOSE, initial, TransposeType.TRANSPOSE, _matrixV);
        if (_processType == NORMALIZE) {
            inverseNormalizeData(res);
        }
        return res;
    }


    public Matrix getTransformationVector() {
        return _matrixV;
    }

    private void calculateMinMaxAvg() {
        this._min = new double[_data.columns()];
        this._max = new double[_data.columns()];
        this._avg = new double[_data.columns()];
        this._sigma = new double[_data.columns()];
        this._olddim = _data.columns();

        for (int j = 0; j < _data.columns(); j++) {
            _min[j] = _data.get(0, j);
            _max[j] = _min[j];
            _avg[j] = _min[j];
            _sigma[j] = _min[j] * _min[j];
        }

        double d;

        for (int i = 1; i < _data.rows(); i++) {
            for (int j = 0; j < _data.columns(); j++) {
                d = _data.get(i, j);
                _avg[j] += d;
                _sigma[j] += d * d;
                if (d < _min[j]) {
                    _min[j] = d;
                } else if (d > _max[j]) {
                    _max[j] = d;
                }
            }
        }

        for (int j = 0; j < _data.columns(); j++) {
            _avg[j] = _avg[j] / _data.rows();
            _sigma[j] = Math.sqrt(_sigma[j]);
        }

//        System.out.println();
//        for(int i=0;i<_data.columns();i++){
//            System.out.println(_min[i]+" , "+_max[i]+" , "+_avg[i]);
//        }
//        System.out.println();

    }


    private static Matrix shiftColumn(Matrix data, double[] shift, boolean workInPlace) {
        Matrix temp = data;
        if (!workInPlace) {
            temp = data.clone();
        }
        for (int i = 0; i < temp.rows(); i++) {
            for (int j = 0; j < temp.columns(); j++) {
                temp.set(i, j, temp.get(i, j) - shift[j]);
            }
        }
        return temp;
    }

    private static Matrix inverseShift(Matrix data, double[] shift, boolean workInPlace) {
        Matrix temp = data;
        if (!workInPlace) {
            temp = data.clone();
        }

        for (int i = 0; i < temp.rows(); i++) {
            for (int j = 0; j < temp.columns(); j++) {
                temp.set(i, j, temp.get(i, j) + shift[j]);
            }
        }
        return temp;
    }


    public int retainDynamic(double[] svector) {
        double d = 0;
        for (int i = 0; i < svector.length; i++) {
            d += svector[i] * svector[i];
        }


        double integrator = 0;
        double previoust = 1;
        double t = 1;
        double p;
        boolean tag=true;
        integrator = svector[0] * svector[0];

        int xi = 0;

        for (int i = 1; i < svector.length; i++) {
            previoust = t;
            t = svector[i] * svector[i] / (svector[i - 1] * svector[i - 1]);
            System.out.println(i + " , " + svector[i] + " , " + t / previoust + " , " + integrator * 100 / d + "%");
            p = integrator * 100 / d;
            if (t / previoust < 0.85 && xi == 0 && i != 1 && p >= 85 && tag) {
                tag=false;
                _percentToRetain = p;
                xi = i;
            }
            integrator += svector[i] * svector[i];
        }
        if (xi == 0) {
            _percentToRetain = integrator * 100 / d;
            xi = svector.length;
        }
        System.out.println(svector.length + " , " + svector[svector.length - 1] + " , " + t / previoust + " , " + integrator * 100 / d + "%");
        System.out.println("");
        return xi;
    }


    public static int retain(double[] svector, double percent) {
        double d = 0;
        for (int i = 0; i < svector.length; i++) {
            d += svector[i] * svector[i];
        }
        d = d * percent;
        double t = 0;
        for (int i = 0; i < svector.length; i++) {
            t += svector[i] * svector[i];
            if (t > d) {
                return i + 1;
            }
        }
        return svector.length;
    }


    public PCA(Matrix data, int processType) {
        this._data = data;
        this._processType = processType;
        calculateMinMaxAvg();

        if (processType == CENTER_ON_AVG) {
            shiftColumn(_data, _avg, true);
        } else if (processType == NORMALIZE) {
            normalizeData(_data);
        }


        //shiftColumn(_data,_avg,true);

//        if (normalize) {
//            normalizeData(_data);
//        }


        _svdDecompose = Matrix.defaultEngine().decomposeSVD(_data, false);

        double[] singularValues = _svdDecompose.getS();


        System.out.println("Singular values");
        for (int i = 0; i < singularValues.length; i++) {
            System.out.println(singularValues[i]);
        }
        System.out.println("");


        System.out.println("Need to retain: " + retainDynamic(singularValues) + " / " + data.columns() + " dimensions");
        System.out.println("Energy retained: " + _percentToRetain + " %");
    }


}
