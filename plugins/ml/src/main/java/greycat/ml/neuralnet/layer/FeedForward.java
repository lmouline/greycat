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
package greycat.ml.neuralnet.layer;

import greycat.Type;
import greycat.ml.common.matrix.MatrixOps;
import greycat.ml.neuralnet.activation.Activation;
import greycat.ml.neuralnet.process.ProcessGraph;
import greycat.ml.neuralnet.process.ExMatrix;
import greycat.ml.neuralnet.activation.Activations;
import greycat.struct.DMatrix;
import greycat.struct.ENode;

import java.util.Random;

public class FeedForward implements Layer {

    private static String WEIGHTS = "weights";
    private static String BIAS = "bias";
    private static String ACTIVATION = "activation";

    private ExMatrix weights;
    private ExMatrix bias;
    private Activation activation;
    private ENode host;


    //Returns ActivationFct( Weights*Input + Bias )
    //Can be seen as simple fully-functional perceptron or neuron
    public FeedForward(ENode hostnode) {
        if (hostnode == null) {
            throw new RuntimeException("Host node can't be null");
        }
        if (hostnode.get(WEIGHTS) != null) {
            weights = new ExMatrix(hostnode, WEIGHTS);
            bias = new ExMatrix(hostnode, BIAS);
            activation = Activations.getUnit((int) hostnode.get(ACTIVATION), null);
        }
        this.host = hostnode;
    }

    public FeedForward create(int inputs, int outputs, int activationUnit, double[] unitArgs) {
        //First always set the type
        host.set(Layers.TYPE, Type.INT, Layers.FEED_FORWARD_LAYER);

        weights = new ExMatrix(host, WEIGHTS);
        weights.init(outputs, inputs);
        bias = new ExMatrix(host, BIAS);
        bias.init(outputs, 1);
        activation = Activations.getUnit(activationUnit, unitArgs);
        host.set(ACTIVATION, Type.INT, activationUnit);
        return this;
    }

    public void setWeights(DMatrix weights) {
        MatrixOps.copy(weights, this.weights);
    }

    public void setBias(DMatrix bias) {
        MatrixOps.copy(bias, this.bias);
    }

    @Override
    public void fillWithRandom(Random random, double min, double max) {
        MatrixOps.fillWithRandom(weights, random, min, max);
        MatrixOps.fillWithRandom(bias, random, min, max);
    }

    @Override
    public void fillWithRandomStd(Random random, double std) {
        MatrixOps.fillWithRandomStd(weights, random, std);
        MatrixOps.fillWithRandomStd(bias, random, std);
    }

    @Override
    public ExMatrix forward(ExMatrix input, ProcessGraph g) {
        ExMatrix sum = g.add(g.mul(weights, input), bias);
        ExMatrix out = g.activate(activation, sum);
        return out;
    }

    @Override
    public void resetState() {
        weights.getDw().fill(0);
        bias.getDw().fill(0);
    }

    @Override
    public ExMatrix[] getModelParameters() {
        return new ExMatrix[]{weights, bias};
    }
}
