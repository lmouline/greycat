package org.mwg.ml.preprocessing;

import org.mwg.ml.algorithm.preprocessing.PCA;
import org.mwg.ml.common.matrix.VolatileMatrix;
import org.mwg.struct.Matrix;

import java.util.Random;

/**
 * Created by assaad on 27/09/16.
 */
public class TestPCA {
    //private static DecimalFormat formatter = new DecimalFormat("#.######");

    /*
    public static void print(Matrix A, String name){
        System.out.println("Matrix "+name);
        for(int r = 0; r < A.getRowDimension(); r++){
            for(int c = 0; c < A.getColumnDimension(); c++){
                System.out.print(formatter.format(A.get(r, c)));
                if (c == A.getColumnDimension()-1) continue;
                System.out.print(", ");
            }
            System.out.println("");
        }
        System.out.println("");
    }*/


    public static void main(String[] arg) {


        int dim = 10;                  // Total dimensions in the data
        int realdim = 3;               // Actual real dimensions in the data, the rest are linear correlation plus some noise
        double randomness = 0.1;       // Strength of the noise from 0 to 1 on the non real dimensions. if randomness ->1 they become somehow real dimension


        int len = dim * 100;  //Number of data point to generate
        double maxsignal = 20; //Maximum signal strength

        Random random = new Random();
//        Matrix.setDefaultEngine(new JamaMatrixEngine());

        Matrix trainingData = VolatileMatrix.empty(len, dim);

        for (int i = 0; i < len; i++) {
            for (int j = 0; j < dim; j++) {
                trainingData.set(i, j, random.nextDouble() * maxsignal);
            }
        }

        for (int i = 0; i < len; i++) {
            for (int j = realdim; j < dim; j++) {
                trainingData.set(i, j, trainingData.get(i, 0) * (1 - randomness) + random.nextDouble() * randomness * maxsignal);
            }
        }

//        for(int i=0;i<len;i++){
//            for(int j=0;j<dim;j++){
//                System.out.println(trainingData.get(i,j));
//            }
//        }

//
//        System.out.println();


        long starttime = System.currentTimeMillis();
        PCA pca = new PCA(trainingData, PCA.NORMALIZE);


        long endtime = System.currentTimeMillis();
        double d = endtime - starttime;
        System.out.println("Analysis took " + d + " ms for a matrix of size: " + trainingData.rows() + "x" + trainingData.columns());

        pca.setDimension(4);

//
//
//        double error=0;
//        for(int i=0;i<lentest;i++){
//            for(int j=0; j< dim; j++){
//                error+=(reversed.get(i,j)-testData.get(i,j))*(reversed.get(i,j)-testData.get(i,j));
//            }
//        }
//
//        error=Math.sqrt(error);
//        System.out.println("error is "+error);


//        System.out.println("Original dim is "+dim+" filtered: "+pca.getOutputDimsNo());


        //print(testData,"Printing original test data:");
        //print(transformedData,"Printing transformed matrix test data:");
        //print(reversed,"Printing reversed matrix test data:");

    }
}
