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
import greycat.ml.common.matrix.TransposeType;
import greycat.struct.DMatrix;

import java.util.ArrayList;
import java.util.List;

public class CalcGraph {

    private boolean applyBackprop;
    private List<ExecutableStep> backprop = new ArrayList<>();

    public CalcGraph(boolean applyBackprop) {
        this.applyBackprop = applyBackprop;
    }

    public final void backpropagate() {
        for (int i = backprop.size() - 1; i >= 0; i--) {
            backprop.get(i).execute();
        }
    }

    //Multiply two matrices
    public final ExMatrix mul(final ExMatrix matA, final ExMatrix matB) {
        final ExMatrix out = ExMatrix.createFromW(MatrixOps.multiply(matA, matB));
        if (this.applyBackprop) {
            ExecutableStep bp = new ExecutableStep() {
                public void execute() {
                    DMatrix dwatemp = MatrixOps.multiplyTranspose(TransposeType.NOTRANSPOSE, out.getDw(), TransposeType.TRANSPOSE, matB.getW());
                    DMatrix dwbtemp = MatrixOps.multiplyTranspose(TransposeType.TRANSPOSE, matA.getW(), TransposeType.NOTRANSPOSE, out.getDw());

                    MatrixOps.addtoMatrix(matA.getDw(), dwatemp);
                    MatrixOps.addtoMatrix(matB.getDw(), dwbtemp);
                }
            };
            backprop.add(bp);
        }
        return out;
    }

    //Add two matrices
    public final ExMatrix add(final ExMatrix matA, final ExMatrix matB) {
        final ExMatrix out = ExMatrix.createFromW(MatrixOps.add(matA, matB));
        if (this.applyBackprop) {
            ExecutableStep bp = new ExecutableStep() {
                //the derivative is distributive over the add operator
                public void execute() {
                    MatrixOps.addtoMatrix(matA.getDw(), out.getDw());
                    MatrixOps.addtoMatrix(matB.getDw(), out.getDw());
                }
            };
            backprop.add(bp);
        }
        return out;
    }


    //Apply activation function
    public final ExMatrix activate(final ActivationUnit activation, final ExMatrix input) {
        final ExMatrix output = ExMatrix.empty(input.rows(), input.columns());
        final int len = input.length();

        //todo [opt] all activation functions can be vectorized as well
        for (int i = 0; i < len; i++) {
            output.unsafeSet(i, activation.forward(input.unsafeGet(i)));
        }

        if (this.applyBackprop) {
            ExecutableStep bp = new ExecutableStep() {
                public void execute() {

                    DMatrix inputDw = input.getDw();
                    DMatrix inputW = input.getW();
                    DMatrix outputDW = output.getDw();
                    DMatrix outputW = output.getW();

                    //todo [opt] can be optimized in // or blas using Hadamard product
                    //Backpropa assigned is: inputDw += derivation of activation * outputDw
                    for (int i = 0; i < len; i++) {
                        inputDw.unsafeSet(i, inputDw.unsafeGet(i) + (activation.backward(inputW.unsafeGet(i), outputW.unsafeGet(i)) * outputDW.unsafeGet(i)));
                    }
                }
            };
            backprop.add(bp);
        }
        return output;
    }

    public final double applyLoss(final LossUnit lossUnit, final ExMatrix actualOutput, final ExMatrix targetOutput) {
        double err = lossUnit.forward(actualOutput, targetOutput);
        if (this.applyBackprop) {
            ExecutableStep bp = new ExecutableStep() {
                public void execute() {
                    lossUnit.backward(actualOutput, targetOutput);
                }
            };
            backprop.add(bp);
        }

        return err;
    }
}
