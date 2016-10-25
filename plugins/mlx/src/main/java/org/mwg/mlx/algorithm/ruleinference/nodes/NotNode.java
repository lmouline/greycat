package org.mwg.mlx.algorithm.ruleinference.nodes;

/**
 * Created by andrey.boytsov on 25/10/2016.
 */
public class NotNode extends BooleanNode {
    private final ConditionGraphNode argNode;

    /**
     * @param argumentNode Argument for NOT
     */
    public NotNode(ConditionGraphNode argumentNode) {
        this.argNode = argumentNode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getBooleanValue() {
        return (!argNode.getBooleanValue());
    }
}
