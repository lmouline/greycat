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
package greycat.ml.neuralnet.layers;

import greycat.ml.neuralnet.ActivationUnit;
import greycat.ml.neuralnet.CalcGraph;
import greycat.ml.neuralnet.ExMatrix;
import greycat.ml.neuralnet.Layer;
import greycat.ml.neuralnet.functions.ActivationUnits;
import greycat.struct.ENode;

import java.util.Random;


public class FeedForwardLayer implements Layer {

    public static String NAME="Feedforward layer";
    private static String WEIGHTS = "ff-weights";
    private static String BIAS = "ff-bias";
    private static String ACTIVATION = "ff-activation";


    private ExMatrix weights;
    private ExMatrix bias;
    private ActivationUnit activation;
    private ENode host;


    public FeedForwardLayer(ENode hostnode) {
        if (hostnode == null) {
            throw new RuntimeException("Host node can't be null");
        }
        if (hostnode.get(WEIGHTS) != null) {
            weights = new ExMatrix(hostnode, WEIGHTS);
            bias = new ExMatrix(hostnode, BIAS);
            activation = ActivationUnits.getUnit((int) hostnode.get(ACTIVATION), null);
        }
        this.host = hostnode;
    }

    public FeedForwardLayer create(int inputs, int outputs, int activationUnit, double[] unitArgs) {

        weights = new ExMatrix(host, WEIGHTS);
        weights.init(outputs, inputs);
        bias = new ExMatrix(host, BIAS);
        bias.init(outputs, 1);
        activation = ActivationUnits.getUnit(activationUnit, unitArgs);

        return this;
    }


    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void fillWithRandom(Random random, double min, double max) {
        weights.fillWithRandom(random, min, max);
        bias.fillWithRandom(random, min, max);
    }

    @Override
    public void fillWithRandomStd(Random random, double std) {
        weights.fillWithRandomStd(random, std);
        bias.fillWithRandomStd(random, std);
    }

    @Override
    public ExMatrix forward(ExMatrix input, CalcGraph g) {
        ExMatrix sum = g.add(g.mul(weights, input), bias);
        ExMatrix out = g.activate(activation, sum);
        return out;
    }

    @Override
    public void resetState() {

    }
}
