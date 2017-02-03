package greycat.ml.common.matrix;

import greycat.struct.DMatrix;

public interface SVDDecompose {
    
    SVDDecompose factor(DMatrix A, boolean workInPlace);

    DMatrix getU();

    DMatrix getVt();

    double[] getS();

    DMatrix getSMatrix();
}
