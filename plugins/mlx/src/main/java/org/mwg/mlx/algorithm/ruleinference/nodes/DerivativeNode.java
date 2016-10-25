package org.mwg.mlx.algorithm.ruleinference.nodes;
import org.mwg.Constants;
import org.mwg.Graph;
import org.mwg.Node;
import org.mwg.ml.algorithm.regression.PolynomialNode;
import org.mwg.task.TaskResult;

import static org.mwg.task.Actions.setWorld;

/**
 * Double value of the node is the derivative d(value)/d(t), where t is measured in MWDB time units
 * (most often - milliseconds).
 *
 * Created by andrey.boytsov on 25/10/2016.
 */
public class DerivativeNode extends DoubleNode {
    //TODO state resolution?

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
     * {@inheritDoc}
     */
    @Override
    public double getDoubleValue() {
        final TaskResult curResult = setWorld(this.world).setTime(""+ Constants.END_OF_TIME).
                lookup(this.nodeId).executeSync(this.graph);
        final double curValue;
        final Node resolvedNode;
        if (curResult.size() > 0){
            resolvedNode = (Node) curResult.get(0);
            if (resolvedNode instanceof PolynomialNode){
                return getPolynomialNodeDerivative((PolynomialNode) resolvedNode);
            }
            curValue = Double.parseDouble(resolvedNode.get(this.attribute).toString());
        }else{
            throw new IllegalStateException("Node not found.");
        }

        final long prevTime = resolvedNode.lastModification()-1;
        final TaskResult prevResult = setWorld(this.world).setTime(""+prevTime).lookup(this.nodeId).executeSync(this.graph);
        final double prevValue;
        final Node prevNode;
        if (prevResult.size() > 0){
            prevNode = (Node) prevResult.get(0);
            prevValue = Double.parseDouble(prevNode.get(this.attribute).toString());
        }else{
            return 0; //No previous state. Counting as no changes
        }

        return (curValue-prevValue)/(resolvedNode.lastModification()-prevNode.lastModification());
    }

    private double getPolynomialNodeDerivative(PolynomialNode node) {
        final double[] weight = (double[]) node.get(PolynomialNode.INTERNAL_WEIGHT_KEY);
        if ((weight==null)||(weight.length <= 1)){
            return 0;
        }
        Long step = (Long) node.get(PolynomialNode.INTERNAL_STEP_KEY);
        if (step == null || step == 0) {
            return 0;
        }
        //At latest sport by construction of polynomila we have derivaive equal to w1
        return weight[1];
    }
}
