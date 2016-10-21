package org.mwg.ml.common.matrix;

import org.mwg.struct.Matrix;

public interface SVDDecompose {
    
    SVDDecompose factor(Matrix A, boolean workInPlace);

    Matrix getU();

    Matrix getVt();

    double[] getS();

    Matrix getSMatrix();
}
