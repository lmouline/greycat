package org.mwg.ml.neuralnet;

import org.mwg.Graph;
import org.mwg.Type;
import org.mwg.plugin.AbstractNode;
import org.mwg.plugin.NodeState;
import org.mwg.struct.Matrix;

public class FlatNeuralNode extends AbstractNode {

    //Inputs are organized in one row


    private static final long NB_INPUTS = 1;
    private static final long NB_OUTPUTS = 2;
    private static final long NB_LAYERS = 3;
    private static final long NB_PER_LAYER = 4;

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

    public FlatNeuralNode configure(int inputs, int outputs, int hiddenlayers, int nodesPerLayer) {
        NodeState state = phasedState();
        state.set(NB_INPUTS, Type.INT, inputs);
        state.set(NB_OUTPUTS, Type.INT, outputs);
        state.set(NB_LAYERS, Type.INT, hiddenlayers);
        state.set(NB_PER_LAYER, Type.INT, nodesPerLayer);


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



            previousDim = nodesPerLayer;
        }


        return this;
    }


}
