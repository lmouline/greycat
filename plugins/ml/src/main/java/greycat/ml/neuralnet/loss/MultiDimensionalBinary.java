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
package greycat.ml.neuralnet.loss;

import greycat.ml.common.matrix.MatrixOps;
import greycat.ml.neuralnet.process.ExMatrix;

class MultiDimensionalBinary implements Loss {

    private static MultiDimensionalBinary static_unit= null;

    public static MultiDimensionalBinary instance() {
        if (static_unit == null) {
            static_unit = new MultiDimensionalBinary();
        }
        return static_unit;
    }


    @Override
    public void backward(ExMatrix actualOutput, ExMatrix targetOutput) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public double forward(ExMatrix actualOutput, ExMatrix targetOutput) {
        MatrixOps.testDim(actualOutput,targetOutput);
        int wlen = actualOutput.length();


        for (int i = 0; i < wlen; i++) {
            if (targetOutput.unsafeGet(i) >= 0.5 && actualOutput.unsafeGet(i) < 0.5) {
                return 1;
            }
            if (targetOutput.unsafeGet(i) < 0.5 && actualOutput.unsafeGet(i) >= 0.5) {
                return 1;
            }
        }
        return 0;
    }

}
