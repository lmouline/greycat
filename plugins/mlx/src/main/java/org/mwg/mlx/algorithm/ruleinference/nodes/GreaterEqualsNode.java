package org.mwg.mlx.algorithm.ruleinference.nodes;

/**
 * Created by andrey.boytsov on 24/10/2016.
 */
public class GreaterEqualsNode extends BooleanNode{
    private final ConditionGraphNode leftSide;
    private final ConditionGraphNode rightSide;

    public GreaterEqualsNode(ConditionGraphNode leftSide, ConditionGraphNode rightSide) {
        this.leftSide = leftSide;
        this.rightSide = rightSide;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getBooleanValue() {
        return (leftSide.getDoubleValue() >= rightSide.getDoubleValue());
    }
}
