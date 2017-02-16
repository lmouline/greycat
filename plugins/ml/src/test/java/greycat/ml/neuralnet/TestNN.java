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
import greycat.ml.neuralnet.activation.Activations;
import greycat.ml.neuralnet.layer.Layers;
import greycat.ml.neuralnet.learner.Learners;
import greycat.ml.neuralnet.loss.Losses;
import greycat.struct.EGraph;
import org.junit.Assert;
import org.junit.Test;

import java.util.Random;

/**
 * Created by assaad on 16/02/2017.
 */
public class TestNN {

    @Test
    public void testLinearNN() {
        Graph g = GraphBuilder.newBuilder().build();
        g.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean result) {

                int input = 5;
                int output = 1;


                int setsize=1000;

                double learningrate=0.3;
                double regularisation=0;

                Node node = g.newNode(0, 0);
                EGraph egraph = (EGraph) node.getOrCreate("nn", Type.EGRAPH);

                NeuralNet net = new NeuralNet(egraph);
                net.setRandom(1234,0.1);

                net.addLayer(Layers.LINEAR_LAYER, input, output, Activations.LINEAR, null);
                net.setLearner(Learners.GRADIENT_DESCENT,new double[]{learningrate,regularisation},1);
                net.setTrainLoss(Losses.SUM_OF_SQUARES);



                Random random=new Random();
                random.setSeed(456);

                double[] inputSet= new double[input];
                double[] outputSet= new double[output];

                for(int i=0;i<setsize;i++){
                    //generate input randomly:
                    outputSet[0]=0;
                    for(int j=0;j<input;j++){
                        inputSet[j]=random.nextDouble();
                        outputSet[0]+=inputSet[j]*j;
                    }
                    double err=net.learn(inputSet, outputSet);

                    if(i%100==0) {
                        System.out.println("Step " + i + " error: " + err);
                    }
                }

                outputSet[0]=0;
                for(int j=0;j<input;j++){
                    inputSet[j]=random.nextDouble();
                    outputSet[0]+=inputSet[j]*j;
                }

                double[] pred=net.predict(inputSet);
                System.out.println("testing target: "+outputSet[0]+" prediction: "+pred[0]);
                Assert.assertTrue(Math.abs(pred[0]-outputSet[0])<1e-10);

            }
        });


    }
}
