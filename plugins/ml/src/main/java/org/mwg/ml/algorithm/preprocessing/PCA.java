package org.mwg.ml.algorithm.preprocessing;

import org.mwg.ml.common.matrix.Matrix;
import org.mwg.ml.common.matrix.SVDDecompose;

/**
 * Created by assaad on 27/09/16.
 */
public class PCA {
    Matrix _data;
    double[] _min;
    double[] _max;
    boolean _normalize;

    SVDDecompose _svdDecompose;



    public static double EPS = 1e-30;

    private void normalizeData(Matrix data) {
        double d = 0;
        for (int j = 0; j < data.columns(); j++) {
            if ((_max[j] - _min[j]) < EPS) {
                for (int i = 0; i < data.rows(); i++) {
                    data.set(i, j, 0);
                }
            } else {
                d = 1 / (_max[j] - _min[j]);
                for (int i = 0; i < data.rows(); i++) {
                    data.set(i, j, (data.get(i, j) - _min[j]) * d);
                }
            }
        }
    }


    private void inverseNormalizeData(Matrix data) {
        double d = 0;
        for (int j = 0; j < data.columns(); j++) {
            if ((_max[j] - _min[j]) < EPS) {
                for (int i = 0; i < data.rows(); i++) {
                    data.set(i, j, _min[j]);
                }
            } else {
                d = _max[j] - _min[j];
                for (int i = 0; i < data.rows(); i++) {
                    data.set(i, j, data.get(i, j) * d + _min[j]);
                }
            }
        }
    }


    private void calculateMinMax() {
        this._min = new double[_data.columns()];
        this._max = new double[_data.columns()];
        for (int j = 0; j < _data.columns(); j++) {
            _min[j] = _data.get(0, j);
            _max[j] = _min[j];
        }

        double d;
        for (int i = 1; i < _data.rows(); i++) {
            for (int j = 0; j < _data.columns(); j++) {
                d = _data.get(i, j);
                if (d < _min[j]) {
                    _min[j] = d;
                } else if (d > _max[j]) {
                    _max[j] = d;
                }
            }
        }
    }




    public PCA(Matrix data, boolean normalize) {
        this._data = data;
        this._normalize = normalize;

        calculateMinMax();
        if (normalize) {
            normalizeData(_data);
        }
        _svdDecompose = Matrix.defaultEngine().decomposeSVD(_data,true);

        int x=0;

    }


}
