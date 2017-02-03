package org.mwg.ml.common.matrix;

import org.mwg.struct.DMatrix;

public interface SVDDecompose {
    
    SVDDecompose factor(DMatrix A, boolean workInPlace);

    DMatrix getU();

    DMatrix getVt();

    double[] getS();

    DMatrix getSMatrix();
}
