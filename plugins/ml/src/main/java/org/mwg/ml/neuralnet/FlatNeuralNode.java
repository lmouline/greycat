package org.mwg.ml.neuralnet;

import org.mwg.Graph;
import org.mwg.Type;
import org.mwg.ml.common.matrix.VolatileMatrix;
import org.mwg.plugin.AbstractNode;
import org.mwg.plugin.NodeState;
import org.mwg.struct.Matrix;

public class FlatNeuralNode extends AbstractNode {

    //Inputs are organized in one row


    private static final long NB_INPUTS = 1;
    private static final long NB_OUTPUTS = 2;
    private static final long NB_LAYERS = 3;
    private static final long NB_PER_LAYER = 4;
    private static final long LEARNING_RATE=5;

    private static final long MATRICES_OFFSET = 10; // matrix offset


    private static Matrix layerWeights(NodeState state, int layer) {
        return (Matrix) state.getOrCreate(MATRICES_OFFSET + layer * 2, Type.MATRIX);
    }

    private static Matrix layerBias(NodeState state, int layer) {
        return (Matrix) state.getOrCreate(MATRICES_OFFSET + layer * 2 + 1, Type.MATRIX);
    }


    public FlatNeuralNode(long p_world, long p_time, long p_id, Graph p_graph) {
        super(p_world, p_time, p_id, p_graph);
    }

    public FlatNeuralNode configure(int inputs, int outputs, int hiddenlayers, int nodesPerLayer, double learningRate) {
        NodeState state = phasedState();
        state.set(NB_INPUTS, Type.INT, inputs);
        state.set(NB_OUTPUTS, Type.INT, outputs);
        state.set(NB_LAYERS, Type.INT, hiddenlayers);
        state.set(NB_PER_LAYER, Type.INT, nodesPerLayer);
        state.set(LEARNING_RATE,Type.DOUBLE,learningRate);


        int previousDim = inputs;
        int nextDim = nodesPerLayer;


        for (int i = 0; i < hiddenlayers; i++) {
            if (i == hiddenlayers - 1) {
                nextDim = outputs;
            }

            Matrix weights = layerWeights(state, i);
            Matrix biases = layerBias(state, i);
            weights.init(previousDim,nextDim);
            biases.init(1,nextDim);
            weights.fillWithRandom(-1.0,1.0,System.currentTimeMillis());
            biases.fillWithRandom(-1.0,1.0,System.currentTimeMillis());

            //initialize randomly here

            previousDim = nodesPerLayer;
        }

        //create output weight and bias after hidden layers index
        Matrix weights = layerWeights(state, hiddenlayers);
        Matrix biases = layerBias(state, hiddenlayers);
        weights.init(previousDim,nextDim);
        biases.init(1,nextDim);
        weights.fillWithRandom(-1.0,1.0,System.currentTimeMillis());
        biases.fillWithRandom(-1.0,1.0,System.currentTimeMillis());

        return this;
    }

    public void learn(final double[] inputs, final double[] outputs) {
        NodeState state = phasedState();

        int nbInput = (int) state.get(NB_INPUTS);
        int nb_output =  (int) state.get(NB_OUTPUTS);
        int nb_hiddenLayers= (int) state.get(NB_LAYERS);
        double learningRate= (double) state.get(LEARNING_RATE);

        Matrix[] integrations=new Matrix[nb_hiddenLayers+1];
        Matrix[] activations=new Matrix[nb_hiddenLayers+1];

        Matrix input=  VolatileMatrix.empty(1,inputs.length);








    }

}
