package greycat.ml.neuralnet;

import greycat.ml.neuralnet.layer.Layer;
import greycat.ml.neuralnet.layer.Layers;
import greycat.ml.neuralnet.process.ProcessGraph;
import greycat.struct.EGraph;

public class NeuralNet {

    private EGraph backend;
    private Layer[] layers;

    public NeuralNet(EGraph p_backend) {
        backend = p_backend;
        int nb = backend.size();
        if (nb > 0) {
            for (int i = 0; i < layers.length; i++) {
                layers[i] = Layers.toLayer(backend.node(i));
            }
        }
        //check the init, first name
    }

    void learn(double[] features, double[] outputs) {
        ProcessGraph cg = new ProcessGraph(true);
        for (int i = 0; i < layers.length; i++) {
            layers[i].forward(null, cg);//TODO pass input
        }
        cg.backpropagate();
    }

    //TODO idem for forward

}
