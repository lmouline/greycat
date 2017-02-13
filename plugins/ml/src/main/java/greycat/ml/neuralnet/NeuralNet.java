package greycat.ml.neuralnet;

import greycat.ml.common.matrix.VolatileDMatrix;
import greycat.ml.neuralnet.layer.Layer;
import greycat.ml.neuralnet.layer.Layers;
import greycat.ml.neuralnet.loss.Loss;
import greycat.ml.neuralnet.loss.Losses;
import greycat.ml.neuralnet.process.ExMatrix;
import greycat.ml.neuralnet.process.ProcessGraph;
import greycat.struct.EGraph;

public class NeuralNet {

    private EGraph backend;
    private Layer[] layers;

    private Loss lossUnit;

    public NeuralNet(EGraph p_backend) {
        backend = p_backend;
        int nb = backend.size();
        if (nb > 0) {
            for (int i = 0; i < layers.length; i++) {
                layers[i] = Layers.toLayer(backend.node(i));
            }
        }

        //todo load loss unit here
        lossUnit= Losses.getUnit(0,null);
        //check the init, first name
    }

    void learn(double[] inputs, double[] outputs) {
        ProcessGraph cg = new ProcessGraph(true);
        ExMatrix input=ExMatrix.createFromW(VolatileDMatrix.wrap(inputs,inputs.length,1));


        ExMatrix targetOutput=ExMatrix.createFromW(VolatileDMatrix.wrap(outputs,outputs.length,1));
        ExMatrix actualOutput=internalForward(cg,input);

        cg.applyLoss(lossUnit,actualOutput,targetOutput);


        cg.backpropagate();
    }

    double[] predict(double[] inputs){
        ProcessGraph cg = new ProcessGraph(false);
        ExMatrix input=ExMatrix.createFromW(VolatileDMatrix.wrap(inputs,inputs.length,1));
        ExMatrix actualOutput=internalForward(cg,input);
        return actualOutput.data();
    }


    ExMatrix internalForward(ProcessGraph cg, ExMatrix input){
        ExMatrix nextInput=input;
        for (int i = 0; i < layers.length; i++) {
            nextInput=layers[i].forward(nextInput, cg);//TODO pass input
        }
        return nextInput;
    }

    //TODO idem for forward

}
