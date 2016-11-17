package org.mwg.ml.neuralnet.bio;

import org.mwg.Callback;
import org.mwg.Graph;
import org.mwg.Node;
import org.mwg.plugin.AbstractNode;
import org.mwg.task.Task;
import org.mwg.task.TaskResult;

import static org.mwg.task.Actions.*;

class BioInputNeuralNode extends AbstractNode {

    static String NAME = "BioInputNeuralNode";

    public BioInputNeuralNode(long p_world, long p_time, long p_id, Graph p_graph) {
        super(p_world, p_time, p_id, p_graph);
    }

    public void learn(final double value, final Callback callback) {

    }

    /*
    private static Task processLayer =
            foreach(
                    defineVar("source")
                            .traverse(BioNeuralNetwork.RELATION_OUTPUTS)
                            .then(context -> {
                                TaskResult<Node> currentNode = context.resultAsNodes();
                                for (int i = 0; i < currentNode.size(); i++) {

                                }
                            })
            );


    private static Task forwardTask = doWhile(processLayer, context -> context.result().size() > 0);
    */

}
