package org.mwg.ml.neuralnet.bio;

import org.mwg.Callback;
import org.mwg.Graph;
import org.mwg.plugin.AbstractNode;

public class BioOutputNeuralNode extends AbstractNode {

    public static String NAME = "BioOutputNeuralNode";

    public BioOutputNeuralNode(long p_world, long p_time, long p_id, Graph p_graph) {
        super(p_world, p_time, p_id, p_graph);
    }

    public void learn(final double value, final Callback callback) {
        //back propagation
    }

}
