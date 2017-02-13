package greycat.ml.neuralnet;

import greycat.ml.neuralnet.layers.Layer;
import greycat.ml.neuralnet.layers.Registry;
import greycat.ml.neuralnet.process.CalcGraph;
import greycat.struct.EGraph;

public class NeuralNet {

    private EGraph backend;

    public NeuralNet(EGraph p_backend) {
        backend = p_backend;
    }

    void learn(double[] features, double[] outputs) {
        CalcGraph cg = new CalcGraph(true);
        int nb = backend.size();
        for (int i = 0; i < nb; i++) {
            final Layer layer = Registry.toLayer(backend.node(i));
            //layer.forward(null, layer);
        }
        cg.backpropagate();
    }

    //TODO idem for forward

}
