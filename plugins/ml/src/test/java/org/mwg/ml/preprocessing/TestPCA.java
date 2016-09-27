package org.mwg.ml.preprocessing;

import org.mwg.ml.algorithm.preprocessing.PCA;
import org.mwg.ml.common.matrix.Matrix;

import java.text.DecimalFormat;
import java.util.Random;

/**
 * Created by assaad on 27/09/16.
 */
public class TestPCA {
    private static DecimalFormat formatter = new DecimalFormat("#.######");

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


    public static void main(String[] arg){


        int dim=80;
        int realdim=60;
        double randomness=0.0001;





        double maxSignal=1;
        int len=1000*dim;
        Random random=new Random();

        int lentest=5;

        double[] temp = new double[len*dim];
//        double[] temptest = new double[lentest*dim];

        for(int i=0;i<len;i++){
            if(i==0){
                for(int j=0;j<dim;j++){
                    temp[i*dim+j]=random.nextDouble()*maxSignal;
                }
            }
            else{
                for(int j=0;j<realdim;j++){
                    temp[i*dim+j]=random.nextDouble()*maxSignal;
                }
                for(int j=realdim;j<dim;j++){
                    temp[i*dim+j]=temp[j]+random.nextDouble()*randomness*maxSignal;
                }
            }
        }

//        double[] temp_back= new double[len*dim];
//        System.arraycopy(temp,0,temp_back,0,len*dim);


//
//        for(int i=0;i<lentest;i++) {
//            for (int j = 0; j < realdim; j++) {
//                temptest[i*dim+j] = random.nextDouble()*maxSignal;
//            }
//            for (int j = realdim; j < dim; j++) {
//                temptest[i*dim+j] = temp[j]+random.nextDouble()*randomness*maxSignal;
//            }
//
//        }



        Matrix trainingData = new Matrix(temp,len,dim);


        long starttime = System.nanoTime();
        PCA pca = new PCA(trainingData,true);
        /** Test data to be transformed. The same convention of representing
         * data points as in the training data matrix is used. */
//        Matrix testData = new Matrix(temptest,lentest,dim);
//        /** The transformed test data. */
//        Matrix transformedData =
//                pca.transform(testData, PCA.TransformationType.ROTATION);
//        Matrix reversed=pca.inverseTransform(transformedData,PCA.TransformationType.ROTATION);
        long endtime = System.nanoTime();
        double d=endtime-starttime;
        d=d/1000000;
        System.out.println("Took "+d +" ms");
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
