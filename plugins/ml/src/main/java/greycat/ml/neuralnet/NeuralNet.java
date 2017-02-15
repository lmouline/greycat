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

import greycat.ml.common.matrix.VolatileDMatrix;
import greycat.ml.neuralnet.layer.Layer;
import greycat.ml.neuralnet.layer.Layers;
import greycat.ml.neuralnet.learner.Learner;
import greycat.ml.neuralnet.learner.Learners;
import greycat.ml.neuralnet.loss.Loss;
import greycat.ml.neuralnet.loss.Losses;
import greycat.ml.neuralnet.process.ExMatrix;
import greycat.ml.neuralnet.process.ProcessGraph;
import greycat.struct.EGraph;

public class NeuralNet {

    public static String LOSS="loss";
    public static String LEARNER="learner";
    public static String LEARNER_PARAMS="learner_params";

    private EGraph backend;
    private Layer[] layers;
    private Loss loss;
    private Learner learner;

    public NeuralNet(EGraph p_backend) {
        backend = p_backend;
        int nb = backend.size();
        layers=new Layer[nb];

        if (nb > 0) {
            //load all layers
            for (int i = 0; i < layers.length; i++) {
                layers[i] = Layers.toLayer(backend.node(i));
            }
            //load NN configuration
            //load loss unit
            loss = Losses.getUnit((int) backend.root().get(LOSS));
            learner= Learners.getUnit((int) backend.root().get(LEARNER), backend.root());

        }
    }

    public final double learn(double[] inputs, double[] outputs) {
        ProcessGraph cg = new ProcessGraph(true);
        ExMatrix input = ExMatrix.createFromW(VolatileDMatrix.wrap(inputs, inputs.length, 1));
        ExMatrix targetOutput = ExMatrix.createFromW(VolatileDMatrix.wrap(outputs, outputs.length, 1));
        ExMatrix actualOutput = internalForward(cg, input);
        double error= cg.applyLoss(loss, actualOutput, targetOutput);
        cg.backpropagate();
        return error;

        //todo add learner


    }

    public final double[] predict(double[] inputs) {
        ProcessGraph cg = new ProcessGraph(false);
        ExMatrix input = ExMatrix.createFromW(VolatileDMatrix.wrap(inputs, inputs.length, 1));
        ExMatrix actualOutput = internalForward(cg, input);
        return actualOutput.data();
    }

    ExMatrix internalForward(ProcessGraph cg, ExMatrix input) {
        ExMatrix nextInput = input;
        for (int i = 0; i < layers.length; i++) {
            nextInput = layers[i].forward(nextInput, cg);//TODO pass input
        }
        return nextInput;
    }

    //TODO idem for forward

}
