package org.mwg.mlx.algorithm.ruleinference.nodes;

import org.mwg.Graph;
import org.mwg.Node;
import org.mwg.task.TaskResult;

import static org.mwg.task.Actions.setWorld;

/**
 * Double value of the node is the derivative d(value)/d(t), where t is measured in seconds.
 *
 * Created by andrey.boytsov on 25/10/2016.
 */
public class DerivativeNode extends DoubleNode {
    private final String nodeId;
    private final String attribute;
    private final Graph graph;
    private final String world;

    /**
     * @param nodeId Node ID
     * @param attribute Attribute of the node
     * @param graph MWDB graph to resolve node from
     * @param world MWDB world
     */
    public DerivativeNode(String nodeId, String attribute, Graph graph, String world) {
        this.nodeId = nodeId;
        this.attribute = attribute;
        this.graph = graph;
        this.world = world;
    }


    /**
     * @return Value of the node, cast to double (+1/-1 for true/false)
     * @throws IllegalStateException If the value cannot be cast to double
     */
    @Override
    public double getDoubleValue() {
        final long curTime = System.currentTimeMillis();
        final TaskResult curResult = setWorld(this.world).setTime(""+curTime).lookup(this.nodeId).executeSync(this.graph);
        final double curValue;
        final Node resolvedNode;
        if (curResult.size() > 0){
            resolvedNode = (Node) curResult.get(0);
            curValue = Double.parseDouble(resolvedNode.get(this.attribute).toString());
        }else{
            throw new IllegalStateException("Node not found.");
        }

        final long prevTime = resolvedNode.lastModification()-1;
        final TaskResult prevResult = setWorld(this.world).setTime(""+prevTime).lookup(this.nodeId).executeSync(this.graph);
        final double prevValue;
        if (prevResult.size() > 0){
            final Node prevNode = (Node) curResult.get(0);
            prevValue = Double.parseDouble(resolvedNode.get(this.attribute).toString());
        }else{
            return 0; //No previous state. Counting as no changes
        }

        return (curValue-prevValue)*1000/(curTime-prevTime); //1000 to transform millis to seconds
    }
}
