/**
 * Copyright 2017 The GreyCat Authors.  All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package greycat.ml.neuralnet;

import greycat.ml.common.matrix.MatrixOps;
/**
 * Created by assaad on 10/02/2017.
 */
public class CalcGraph {


    private boolean applyBackprop;
    //private List<Runnable> backprop = new ArrayList<>();


    public CalcGraph(boolean applyBackprop) {
        this.applyBackprop = applyBackprop;
    }


    //Multiply two matrices
    public ExMatrix mul(ExMatrix matA, ExMatrix matB) {

        ExMatrix out = ExMatrix.createFromW(MatrixOps.multiply(matA, matB));


        return null;
    }


    //Add two matrices
    public ExMatrix add(ExMatrix matA, ExMatrix matB) {
        return null;
    }


    //Apply activation function
    public ExMatrix activate(ActivationUnit activation, ExMatrix input) {
        final ExMatrix output = ExMatrix.empty(input.rows(), input.columns());
        final int len = input.length();

        //todo all activation functions can be vectorized as well
        for (int i = 0; i < len; i++) {
            output.unsafeSet(i, activation.forward(input.unsafeGet(i)));
        }

        if (this.applyBackprop) {
       /*     Runnable bp = new Runnable() {
                public void run() {
                    DMatrix inputDw = input.getDw();
                    DMatrix inputW = input.getW();
                    DMatrix outputDW = output.getDw();
                    DMatrix outputW = output.getW();

                    //toDo can be optimized in // or blas using Hadamard product
                    //Backpropa assigned is: inputDw += derivation of activation
                    for (int i = 0; i < len; i++) {
                        inputDw.unsafeSet(i, inputDw.unsafeGet(i) + (activation.backward(inputW.unsafeGet(i), outputW.unsafeGet(i)) * outputDW.unsafeGet(i)));
                    }
                }
            };
            backprop.add(bp);*/
        }


        return output;
    }
}
