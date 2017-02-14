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
package greycat.ml.neuralnet.learner;


import greycat.ml.common.matrix.MatrixOps;
import greycat.ml.neuralnet.layer.Layer;
import greycat.ml.neuralnet.process.ExMatrix;
import greycat.struct.DMatrix;

public class SGD implements Learner {
    private double learningRate;
    private double regRate;

    //param[0] => learning rate
    //param[1] => regularization rate
    public SGD(double[] params) {
        learningRate=params[0];
        regRate=(1 - learningRate * params[1]);
    }

    public void oneStepUpdate(Layer[] layers) {
        DMatrix w;
        DMatrix dw;
        int len;
        for (int i = 0; i < layers.length; i++) {
            ExMatrix[] weights = layers[i].getModelParameters();
            for (int j = 0; j < weights.length; j++) {
                w = weights[j].getW();
                dw = weights[j].getDw();

                //w= (1-alpha*Lambda)*w - learningRate * dw ;
                MatrixOps.addInPlace(w, regRate , dw, -learningRate);


            }
        }
    }

    public void finalUpdate(Layer[] layers) {

    }


}
