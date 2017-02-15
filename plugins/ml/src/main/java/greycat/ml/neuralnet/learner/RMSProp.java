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
import greycat.struct.ENode;

/**
 * Created by assaad on 14/02/2017.
 */
public class RMSProp extends AbstractLearner {

    public static String LEARNING_RATE = "learningrate";
    public static double LEARNING_RATE_DEF = 0.001;

    public static String REGULARIZATION_RATE = "regularizationrate";
    public static double REGULARIZATION_RATE_DEF = 0.000001;

    public static String SMOOTH_EPSILON = "smoothepsilon";
    public static double SMOOTH_EPSILON_DEF = 1e-8;

    public static String DECAY_RATE = "decayrate";
    public static double DECAY_RATE_DEF = 0.999;

    public static String GRADIENT_CLIP_RATE = "gradientclip";
    public static double GRADIENT_CLIP_DEF = 5;

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
    public RMSProp(ENode backend) {
        super(backend);
        learningRate = backend.getWithDefault(LEARNING_RATE, LEARNING_RATE_DEF);
        regularization = backend.getWithDefault(REGULARIZATION_RATE, REGULARIZATION_RATE_DEF);
        smoothEpsilon = backend.getWithDefault(SMOOTH_EPSILON, SMOOTH_EPSILON_DEF);
        decayRate = backend.getWithDefault(DECAY_RATE, DECAY_RATE_DEF);
        gradientClip = backend.getWithDefault(GRADIENT_CLIP_RATE, GRADIENT_CLIP_DEF);
    }


    @Override
    protected void update(Layer[] layers) {
        DMatrix w;
        DMatrix dw;
        DMatrix stepCache;
        int len;
        double dwi;
        double alpha = 1 - learningRate * regularization / steps;

        for (int k = 0; k < layers.length; k++) {
            ExMatrix[] weights = layers[k].getModelParameters();
            for (int j = 0; j < weights.length; j++) {
                w = weights[j].getW();
                dw = weights[j].getDw();
                stepCache = weights[j].getStepCache();
                len = w.length();

                for (int i = 0; i < len; i++) {

                    // rmsprop adaptive learning rate
                    dwi = dw.unsafeGet(i) / steps;
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
