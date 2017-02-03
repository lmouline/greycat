package greycat.ml.common.matrix;

import greycat.struct.DMatrix;


public class MatrixOps {

    private static MatrixEngine _defaultEngine = null;
    
    public static MatrixEngine defaultEngine() {
        if (_defaultEngine == null) {
            _defaultEngine = new HybridMatrixEngine();
        }
        return _defaultEngine;
    }

    public static void setDefaultEngine(MatrixEngine engine){
        _defaultEngine=engine;
    }


    public static void copyMatrix(DMatrix source, DMatrix destination) {
        for (int row = 0; row < source.rows(); row++) {
            for(int col=0;col<source.columns();col++){
                destination.set(row,col,source.get(row,col));
            }
        }
    }

    public static DMatrix multiply(DMatrix matA, DMatrix matB) {
        return defaultEngine().multiplyTransposeAlphaBeta(TransposeType.NOTRANSPOSE, 1d, matA, TransposeType.NOTRANSPOSE, matB, 0, null);
    }

    public static DMatrix multiplyTranspose(TransposeType transA, DMatrix matA, TransposeType transB, DMatrix matB) {
        return defaultEngine().multiplyTransposeAlphaBeta(transA, 1.0, matA, transB, matB, 0, null);
    }

    public static DMatrix multiplyTransposeAlpha(TransposeType transA, double alpha, DMatrix matA, TransposeType transB, DMatrix matB) {
        return defaultEngine().multiplyTransposeAlphaBeta(transA, alpha, matA, transB, matB, 0, null);
    }

    public static DMatrix multiplyTransposeAlphaBeta(TransposeType transA, double alpha, DMatrix matA, TransposeType transB, DMatrix matB, double beta, DMatrix matC) {
        return defaultEngine().multiplyTransposeAlphaBeta(transA, alpha, matA, transB, matB, beta, matC);
    }

    public static DMatrix invert(DMatrix mat, boolean invertInPlace) {
        return defaultEngine().invert(mat, invertInPlace);
    }

    public static DMatrix pinv(DMatrix mat, boolean invertInPlace) {
        return defaultEngine().pinv(mat, invertInPlace);
    }



    public static void scale(double alpha, VolatileDMatrix matA) {
        if (alpha == 0) {
            matA.fill(0);
            return;
        }
        for (int i = 0; i < matA.rows() * matA.columns(); i++) {
            matA.unsafeSet(i, alpha * matA.unsafeGet(i));
        }
    }

    public static DMatrix transpose(DMatrix matA) {
        DMatrix result =  VolatileDMatrix.empty(matA.columns(), matA.rows());
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

    private static void transposeSquare(DMatrix matA, DMatrix result) {
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

    private static void transposeStandard(DMatrix matA, DMatrix result) {
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

    private static void transposeBlock(DMatrix matA, DMatrix result) {
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


    public static boolean testDimensionsAB(TransposeType transA, TransposeType transB, DMatrix matA, DMatrix matB) {
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


    public static DMatrix sub(DMatrix matA, DMatrix matB) {
        if(matA.rows()!=matB.rows()|| matA.columns()!=matB.columns()){
            throw new RuntimeException("Matrices A and B have different dimensions for the substract operation");
        }
        DMatrix result= VolatileDMatrix.empty(matA.rows(),matA.columns());
        int total=matA.rows()*matA.columns();
        for(int i=0;i<total;i++){
            result.unsafeSet(i,matA.unsafeGet(i)-matB.unsafeGet(i));
        }
        return result;
    }

    public static DMatrix add(DMatrix matA, DMatrix matB) {
        if(matA.rows()!=matB.rows()|| matA.columns()!=matB.columns()){
            throw new RuntimeException("Matrices A and B have different dimensions for the add operation");
        }
        DMatrix result= VolatileDMatrix.empty(matA.rows(),matA.columns());
        int total=matA.rows()*matA.columns();
        for(int i=0;i<total;i++){
            result.unsafeSet(i,matA.unsafeGet(i)+matB.unsafeGet(i));
        }
        return result;
    }
}
