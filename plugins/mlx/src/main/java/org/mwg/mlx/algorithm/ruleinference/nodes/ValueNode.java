package org.mwg.mlx.algorithm.ruleinference.nodes;

import org.mwg.Graph;
import org.mwg.Node;
import org.mwg.task.TaskResult;

import static org.mwg.task.Actions.setWorld;

/**
 * Created by andrey.boytsov on 25/10/2016.
 */
public class ValueNode implements ConditionGraphNode{
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
    public ValueNode(String nodeId, String attribute, Graph graph, String world) {
        this.nodeId = nodeId;
        this.attribute = attribute;
        this.graph = graph;
        this.world = world;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getValue() {
        TaskResult result = setWorld(this.world).setTime(""+System.currentTimeMillis()).
                lookup(this.nodeId).executeSync(this.graph);
        if (result.size() > 0){
            Node resolvedNode = (Node) result.get(0);
            return resolvedNode.get(this.attribute).toString();
        }
        //TODO Not resolved. What to do? Currently - default to null.
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getBooleanValue() {
        final String val = getValue();
        if (val == null){
            throw new IllegalStateException("Can't cast null value to boolean");
        }
        if (BooleanNode.TRUE_STR.toLowerCase().equals(val)){
            return true;
        }
        if (BooleanNode.FALSE_STR.toLowerCase().equals(val)){
            return false;
        }
        try {
            return Double.parseDouble(val) > 0;
        } catch(NumberFormatException e) {
            throw new IllegalStateException("Can't cast value to double: " + val);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getDoubleValue() {
        final String val = getValue();
        if (val == null){
            throw new IllegalStateException("Can't cast null value to double");
        }
        if (BooleanNode.TRUE_STR.toLowerCase().equals(val)) {
            return BooleanNode.TRUE_DOUBLE;
        }
        if (BooleanNode.FALSE_STR.toLowerCase().equals(val)) {
            return BooleanNode.FALSE_DOUBLE;
        }
        try {
            return Double.parseDouble(val);
        } catch(NumberFormatException e){
            throw new IllegalStateException("Can't cast value to double: "+val);
        }
    }
}
