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

import greycat.*;
import greycat.ml.common.matrix.VolatileDMatrix;
import greycat.ml.neuralnet.activation.Activations;
import greycat.ml.neuralnet.layer.FeedForwardLayer;
import greycat.ml.neuralnet.loss.Loss;
import greycat.ml.neuralnet.loss.Losses;
import greycat.ml.neuralnet.process.ProcessGraph;
import greycat.ml.neuralnet.process.ExMatrix;
import greycat.struct.DMatrix;
import greycat.struct.EGraph;
import greycat.struct.ENode;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by assaad on 13/02/2017.
 */
public class TestFeedForward {
    @Test
    public void testcalc() {

        //Simulating example found in: https://mattmazur.com/2015/03/17/a-step-by-step-backpropagation-example/


        Graph g = GraphBuilder.newBuilder().build();

        g.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean result) {
                //input matrix
                DMatrix input = VolatileDMatrix.empty(2, 1);
                input.set(0, 0, 0.05);
                input.set(1, 0, 0.1);

                //output matrix
                DMatrix targetOutput = VolatileDMatrix.empty(2, 1);
                targetOutput.set(0, 0, 0.01);
                targetOutput.set(1, 0, 0.99);


                //weights matrix 1
                DMatrix weights1 = VolatileDMatrix.empty(2, 2);
                weights1.set(0, 0, 0.15);
                weights1.set(0, 1, 0.2);
                weights1.set(1, 0, 0.25);
                weights1.set(1, 1, 0.3);

                //bias matrix 1
                DMatrix bias1 = VolatileDMatrix.empty(2, 1);
                bias1.set(0, 0, 0.35);
                bias1.set(1, 0, 0.35);

                //weights matrix 2
                DMatrix weights2 = VolatileDMatrix.empty(2, 2);
                weights2.set(0, 0, 0.4);
                weights2.set(0, 1, 0.45);
                weights2.set(1, 0, 0.5);
                weights2.set(1, 1, 0.55);

                //bias matrix 2
                DMatrix bias2 = VolatileDMatrix.empty(2, 1);
                bias2.set(0, 0, 0.6);
                bias2.set(1, 0, 0.6);

                Loss sumsq = Losses.getUnit(Losses.SUM_OF_SQUARES);

                Node node = g.newNode(0, 0);
                EGraph nngraph = (EGraph) node.getOrCreate("nn", Type.EGRAPH);
                ENode l1node = nngraph.newNode();
                ENode l2node = nngraph.newNode();

                FeedForwardLayer layer1 = new FeedForwardLayer(l1node);
                layer1.create(2, 2, Activations.SIGMOID, null);
                FeedForwardLayer layer2 = new FeedForwardLayer(l2node);
                layer2.create(2, 2, Activations.SIGMOID, null);

                layer1.setWeights(weights1);
                layer1.setBias(bias1);
                layer2.setWeights(weights2);
                layer2.setBias(bias2);

                ProcessGraph calcgraph = new ProcessGraph(true);
                ExMatrix actualOutput = layer2.forward(layer1.forward(ExMatrix.createFromW(input), calcgraph), calcgraph);


                //The calculation steps are here


                double err = calcgraph.applyLoss(sumsq, actualOutput, ExMatrix.createFromW(targetOutput));
                //System.out.println("Error: "+err);
                testdouble(err, 0.2983711087600027);

                //Now the backpropagation step:
                calcgraph.backpropagate();
                ExMatrix[] ww1 = layer1.getModelParameters();
                ExMatrix[] ww2 = layer2.getModelParameters();
                for(ExMatrix www: ww1){
                    applyLearningRate(www,0.5);
                }
                for(ExMatrix www: ww2){
                    applyLearningRate(www,0.5);
                }

                weights1 = ww1[0];
                bias1 = ww1[1];
                weights2 = ww2[0];
                bias2 = ww2[1];


                //Let's apply the learning rate: 0.5
                double learningRate = 0.5;


                testdouble(weights1.get(0, 0), 0.1497807161327628);
                testdouble(weights1.get(0, 1), 0.19956143226552567);
                testdouble(weights1.get(1, 0), 0.24975114363236958);
                testdouble(weights1.get(1, 1), 0.29950228726473915);

                testdouble(bias1.get(0, 0), 0.3456143226552565);
                testdouble(bias1.get(1, 0), 0.3450228726473914);

                testdouble(weights2.get(0, 0), 0.35891647971788465);
                testdouble(weights2.get(0, 1), 0.4086661860762334);
                testdouble(weights2.get(1, 0), 0.5113012702387375);
                testdouble(weights2.get(1, 1), 0.5613701211079891);

                testdouble(bias2.get(0, 0), 0.5307507191857215);
                testdouble(bias2.get(1, 0), 0.6190491182582781);


            }
        });

    }

    private static double EPS = 1e-16;

    private static void testdouble(double d1, double d2) {

        //Assert.assertTrue(Math.abs(d1 - d2) < EPS);
        if(Math.abs(d1 - d2) > EPS){
            System.out.println("d1: "+d1+ " d2: "+d2);
            throw new RuntimeException("d1 != d2");
        }
    }

    private static void applyLearningRate(ExMatrix mat, double learningRate) {
        int len = mat.length();
        DMatrix dw = mat.getDw();
        for (int i = 0; i < len; i++) {
            mat.unsafeSet(i, mat.unsafeGet(i) - learningRate * dw.unsafeGet(i));
        }
        //Empty the learning
        dw.fill(0);
    }
}
