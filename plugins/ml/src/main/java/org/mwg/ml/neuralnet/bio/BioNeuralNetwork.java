package org.mwg.ml.neuralnet.bio;

import org.mwg.*;
import org.mwg.plugin.AbstractNode;
import org.mwg.struct.LongLongMap;
import org.mwg.struct.LongLongMapCallBack;
import org.mwg.struct.Matrix;
import org.mwg.struct.Relationship;

public class BioNeuralNetwork extends AbstractNode {

    public static String NAME = "BioNeuralNetworkNode";
    public static final String RELATION_INPUTS = "inputs";
    private static final String RELATION_INPUTS_MAP = "inputs_map";
    public static final String RELATION_OUTPUTS = "outputs";
    private static final String RELATION_OUTPUTS_MAP = "outputs_map";

    public static final String BUFFER = "buffer";
    public static final int BUFFER_SUM_INDEX = 0;
    public static final int BUFFER_NB_INDEX = 1;

    public static final String WEIGHTS = "weights";

    public BioNeuralNetwork(long p_world, long p_time, long p_id, Graph p_graph) {
        super(p_world, p_time, p_id, p_graph);
    }

    @SuppressWarnings("Duplicates")
    public BioNeuralNetwork configure(final int inputs, final int outputs, int hiddenlayers, final int nodesPerLayer, final int spikeBuffer) {
        Node[] inputNodes = new BioInputNeuralNode[inputs];
        //create input layer
        for (int i = 0; i < inputs; i++) {
            BioInputNeuralNode input = (BioInputNeuralNode) graph().newTypedNode(world(), time(), BioInputNeuralNode.NAME);
            inputNodes[i] = input;
            this.add(RELATION_INPUTS, input);
        }
        BioNeuralNode[] previousLayer = new BioNeuralNode[nodesPerLayer];
        int seed = 0;
        //create hidden layers
        for (int i = 0; i < hiddenlayers; i++) {
            //first hidden layer
            if (i == 0) {
                for (int j = 0; j < nodesPerLayer; j++) {
                    final BioNeuralNode neuralNode = (BioNeuralNode) graph().newTypedNode(world(), time(), BioNeuralNode.NAME);
                    //init buffer and weights
                    Matrix buffer = (Matrix) neuralNode.getOrCreate(BUFFER, Type.MATRIX);
                    buffer.init(inputs, 2);
                    buffer.fill(0d);
                    Matrix weights = (Matrix) neuralNode.getOrCreate(WEIGHTS, Type.MATRIX);
                    weights.init(inputs, 1);
                    weights.fillWithRandom(-1d, 1d, seed);
                    seed++;
                    previousLayer[j] = neuralNode;
                    for (int k = 0; k < inputs; k++) {
                        ((LongLongMap) inputNodes[k].getOrCreate(RELATION_OUTPUTS, Type.LONG_TO_LONG_MAP)).put(neuralNode.id(), j);
                        ((LongLongMap) neuralNode.getOrCreate(RELATION_INPUTS, Type.LONG_TO_LONG_MAP)).put(inputNodes[k].id(), k);
                    }
                }
                //clear input
                graph().freeNodes(inputNodes);
                inputNodes = null;
            } else {
                //create the new temp layer
                final BioNeuralNode[] tempLayer = new BioNeuralNode[nodesPerLayer];
                for (int j = 0; j < nodesPerLayer; j++) {
                    final BioNeuralNode neuralNode = (BioNeuralNode) graph().newTypedNode(world(), time(), BioNeuralNode.NAME);
                    //init buffer and weights
                    Matrix buffer = (Matrix) neuralNode.getOrCreate(BUFFER, Type.MATRIX);
                    buffer.init(nodesPerLayer, 2);
                    buffer.fill(0d);
                    Matrix weights = (Matrix) neuralNode.getOrCreate(WEIGHTS, Type.MATRIX);
                    weights.init(nodesPerLayer, 1);
                    weights.fillWithRandom(-1d, 1d, seed);
                    seed++;
                    tempLayer[j] = neuralNode;
                    for (int k = 0; k < nodesPerLayer; k++) {
                        ((LongLongMap) previousLayer[k].getOrCreate(RELATION_OUTPUTS, Type.LONG_TO_LONG_MAP)).put(neuralNode.id(), j);
                        ((LongLongMap) neuralNode.getOrCreate(RELATION_INPUTS, Type.LONG_TO_LONG_MAP)).put(previousLayer[k].id(), k);
                    }
                }
                graph().freeNodes(previousLayer);
                previousLayer = tempLayer;
            }
        }
        //create output layer
        for (int i = 0; i < outputs; i++) {
            BioOutputNeuralNode output = (BioOutputNeuralNode) graph().newTypedNode(world(), time(), BioOutputNeuralNode.NAME);
            //init buffer and weights
            Matrix buffer = (Matrix) output.getOrCreate(BUFFER, Type.MATRIX);
            buffer.init(nodesPerLayer, 2);
            buffer.fill(0d);
            Matrix weights = (Matrix) output.getOrCreate(WEIGHTS, Type.MATRIX);
            weights.init(nodesPerLayer, 1);
            weights.fillWithRandom(-1d, 1d, seed);
            seed++;
            for (int k = 0; k < nodesPerLayer; k++) {
                ((LongLongMap) previousLayer[k].getOrCreate(RELATION_OUTPUTS, Type.LONG_TO_LONG_MAP)).put(output.id(), i);
                ((LongLongMap) output.getOrCreate(RELATION_INPUTS, Type.LONG_TO_LONG_MAP)).put(previousLayer[k].id(), k);
            }
            this.add(RELATION_OUTPUTS, output);
        }
        graph().freeNodes(previousLayer);
        return this;
    }

    public void learn(final long inputCode, final boolean isInput, final double value, final Callback callbacks) {
        //TODO atomic method
        final LongLongMap mapping;
        if (isInput) {
            mapping = (LongLongMap) this.getOrCreate(RELATION_INPUTS_MAP, Type.LONG_TO_LONG_MAP);
        } else {
            mapping = (LongLongMap) this.getOrCreate(RELATION_OUTPUTS_MAP, Type.LONG_TO_LONG_MAP);
        }
        long previousNode = mapping.get(inputCode);
        if (previousNode == Constants.NULL_LONG) {
            final Relationship relationship;
            if (isInput) {
                relationship = (Relationship) this.get(RELATION_INPUTS);
            } else {
                relationship = (Relationship) this.get(RELATION_OUTPUTS);
            }
            if (relationship == null) {
                throw new RuntimeException("Bad API usage, please call configure method first!");
            }
            if (relationship.size() == mapping.size()) {
                throw new RuntimeException("All input/output has been consumed, please reset the " + NAME);
            }
            for (int i = 0; i < relationship.size(); i++) {
                long relationVal = relationship.get(i);
                final boolean[] isPresent = {false};
                mapping.each(new LongLongMapCallBack() {
                    @Override
                    public void on(long key, long value) {
                        if (value == relationVal) {
                            isPresent[0] = true;
                        }
                    }
                });
                if (!isPresent[0]) {
                    previousNode = relationVal;
                    break;
                }
            }
        }
        if (previousNode == Constants.NULL_LONG) {
            throw new RuntimeException("Internal error exception!");
        }
        graph().lookup(world(), time(), previousNode, new Callback<Node>() {
            @Override
            public void on(Node result) {
                if (result == null) {
                    throw new RuntimeException("Internal error exception!");
                } else {
                    if (isInput) {
                        ((BioInputNeuralNode) result).learn(value, callbacks);
                    } else {
                        ((BioOutputNeuralNode) result).learn(value, callbacks);
                    }
                }
            }
        });
    }

}
