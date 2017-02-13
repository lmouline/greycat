package greycat.ml.neuralnet;

import greycat.ml.neuralnet.layer.Layer;
import greycat.ml.neuralnet.layer.Layers;
import greycat.ml.neuralnet.process.ProcessGraph;
import greycat.struct.EGraph;

public class NeuralNet {

    private EGraph backend;

    public NeuralNet(EGraph p_backend) {
        backend = p_backend;
    }

    void learn(double[] features, double[] outputs) {
        ProcessGraph cg = new ProcessGraph(true);
        int nb = backend.size();
        for (int i = 0; i < nb; i++) {
            final Layer layer = Layers.toLayer(backend.node(i));
            //layer.forward(null, layer);
        }
        cg.backpropagate();
    }

    //TODO idem for forward

}
