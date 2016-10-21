package org.mwg.ml.common;

import org.junit.Assert;
import org.junit.Test;
import org.mwg.ml.common.matrix.VolatileMatrix;
import org.mwg.ml.common.matrix.MatrixEngine;
import org.mwg.ml.common.matrix.TransposeType;
import org.mwg.ml.common.matrix.blassolver.BlasMatrixEngine;
import org.mwg.ml.common.matrix.jamasolver.JamaMatrixEngine;
import org.mwg.struct.Matrix;

public class MultiplyTest {

    @Test
    public void MatrixMultBlas() {
        InternalManualMult(new BlasMatrixEngine());
    }

    @Test
    public void MatrixMultJama() {
        InternalManualMult(new JamaMatrixEngine());
    }

    public Matrix manualMultpily(Matrix matA, Matrix matB) {
        Matrix matC = VolatileMatrix.empty(matA.rows(), matB.columns());
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
        Matrix matA = VolatileMatrix.random(r, o, 0, 100);
        Matrix matB = VolatileMatrix.random(o, p, 0, 100);

        Matrix result = engine.multiplyTransposeAlphaBeta(TransposeType.NOTRANSPOSE, 1, matA, TransposeType.NOTRANSPOSE, matB, 0, null);
        Matrix matD = manualMultpily(matA, matB);

        double eps = 1e-7;

        for (int i = 0; i < r; i++) {
            for (int j = 0; j < p; j++) {
                Assert.assertTrue(Math.abs(result.get(i, j) - matD.get(i, j)) < eps);
            }
        }

        //  System.out.println(System.currentTimeMillis() - current);

    }
}
