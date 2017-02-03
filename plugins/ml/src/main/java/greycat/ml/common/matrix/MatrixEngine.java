package greycat.ml.common.matrix;

import greycat.struct.DMatrix;

public interface MatrixEngine {

    DMatrix multiplyTransposeAlphaBeta(TransposeType transA, double alpha, DMatrix matA, TransposeType transB, DMatrix matB, double beta, DMatrix matC);

    DMatrix invert(DMatrix mat, boolean invertInPlace);

    DMatrix pinv(DMatrix mat, boolean invertInPlace);

    //Solve AX=B -> return X as a result
    DMatrix solveLU(DMatrix matA, DMatrix matB, boolean workInPlace, TransposeType transB);

    DMatrix solveQR(DMatrix matA, DMatrix matB, boolean workInPlace, TransposeType transB);

    SVDDecompose decomposeSVD(DMatrix matA, boolean workInPlace);

    DMatrix solve(DMatrix A, DMatrix B);

}
