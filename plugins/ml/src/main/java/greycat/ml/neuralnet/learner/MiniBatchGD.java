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

/**
 * Created by assaad on 14/02/2017.
 */
public class MiniBatchGD implements Learner {
    private double learningRate;
    private double regularizationRate;
    private int batchSize;
    private int counter;


    //param[0] => learning rate
    //param[1] => regularization rate
    //param[2] => batch size
    public MiniBatchGD(double[] params) {
        learningRate = params[0];
        regularizationRate = params[1];
        batchSize = (int) params[2];
        if (batchSize <= 0) {
            throw new RuntimeException("Batch size can't be <0");
        }
        counter = 0;
    }

    @Override
    public void stepUpdate(Layer[] layers) {
        counter++;
        if (counter == batchSize) {
            SGD.update(layers, batchSize, learningRate, regularizationRate);
            counter = 0;
        }
    }

    @Override
    public void finalUpdate(Layer[] layers) {

    }
}
