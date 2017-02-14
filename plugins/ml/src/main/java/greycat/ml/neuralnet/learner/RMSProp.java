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

import greycat.ml.neuralnet.layer.Layer;
import greycat.ml.neuralnet.process.ExMatrix;
import greycat.struct.DMatrix;

/**
 * Created by assaad on 14/02/2017.
 */
public class RMSProp extends AbstractLearner {


    private double learningRate;
    private double regularization;
    private double smoothEpsilon;
    private double decayRate;
    private double gradientClip;


    //param[0] => learning rate
    //param[1] => regularization rate
    //param[2] => smooth Epsilon
    //param[2] => decay Rate
    //param[2] => gradient Clip
    public RMSProp(double[] params) {
        super();
        learningRate = params[0];
        regularization = params[1];
        smoothEpsilon = params[2];
        decayRate = params[3];
        gradientClip = params[4];
    }


    @Override
    protected void update(Layer[] layers) {
        DMatrix w;
        DMatrix dw;
        DMatrix stepCache;
        int len;
        double dwi;
        double alpha = 1 - learningRate * regularization / numberOfSamples;

        for (int k = 0; k < layers.length; k++) {
            ExMatrix[] weights = layers[k].getModelParameters();
            for (int j = 0; j < weights.length; j++) {
                w = weights[j].getW();
                dw = weights[j].getDw();
                stepCache = weights[j].getStepCache();
                len = w.length();

                for (int i = 0; i < len; i++) {

                    // rmsprop adaptive learning rate
                    dwi = dw.unsafeGet(i) / numberOfSamples;
                    stepCache.unsafeSet(i, stepCache.unsafeGet(i) * decayRate + (1 - decayRate) * dwi * dwi);

                    // gradient clip
                    if (dwi > gradientClip) {
                        dwi = gradientClip;
                    }
                    if (dwi < -gradientClip) {
                        dwi = -gradientClip;
                    }

                    // update (and regularize)
                    w.unsafeSet(i, w.unsafeGet(i) * alpha - learningRate * dwi / Math.sqrt(stepCache.unsafeGet(i) + smoothEpsilon));
                }
                dw.fill(0);
            }
        }
    }
}
