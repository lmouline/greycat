package greycat.ml.common;

import greycat.ml.common.matrix.MatrixEngine;
import greycat.ml.common.matrix.TransposeType;
import greycat.ml.common.matrix.blassolver.BlasMatrixEngine;
import greycat.ml.common.matrix.jamasolver.JamaMatrixEngine;
import org.junit.Assert;
import org.junit.Test;
import greycat.ml.common.matrix.VolatileDMatrix;
import greycat.struct.DMatrix;

public class MultiplyTest {

    @Test
    public void MatrixMultBlas() {
        InternalManualMult(new BlasMatrixEngine());
    }

    @Test
    public void MatrixMultJama() {
        InternalManualMult(new JamaMatrixEngine());
    }

    public DMatrix manualMultpily(DMatrix matA, DMatrix matB) {
        DMatrix matC = VolatileDMatrix.empty(matA.rows(), matB.columns());
        for (int i = 0; i < matA.rows(); i++) {
            for (int j = 0; j < matB.columns(); j++) {
                for (int k = 0; k < matA.columns(); k++) {
                    matC.add(i, j, matA.get(i, k) * matB.get(k, j));
                }
            }
        }

        return matC;
    }

    public void InternalManualMult(MatrixEngine engine) {

        //  long current = System.currentTimeMillis();

        //Test matrix mult
        int r = 30;
        int o = 30;
        int p = 30;
        DMatrix matA = VolatileDMatrix.random(r, o, 0, 100);
        DMatrix matB = VolatileDMatrix.random(o, p, 0, 100);

        DMatrix result = engine.multiplyTransposeAlphaBeta(TransposeType.NOTRANSPOSE, 1, matA, TransposeType.NOTRANSPOSE, matB, 0, null);
        DMatrix matD = manualMultpily(matA, matB);

        double eps = 1e-7;

        for (int i = 0; i < r; i++) {
            for (int j = 0; j < p; j++) {
                Assert.assertTrue(Math.abs(result.get(i, j) - matD.get(i, j)) < eps);
            }
        }

        //  System.out.println(System.currentTimeMillis() - current);

    }
}
