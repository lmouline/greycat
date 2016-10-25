package org.mwg.mlx.algorithm.ruleinference.nodes;

/**
 * Should work for double, boolean or string values.
 * Values of different types (e.g. boolean vs double) are not equal.
 *
 * Created by andrey.boytsov on 24/10/2016.
 */
public class EqualsNode extends BooleanNode {
    private final ConditionGraphNode leftSide;
    private final ConditionGraphNode rightSide;

    public EqualsNode(ConditionGraphNode leftSide, ConditionGraphNode rightSide) {
        this.leftSide = leftSide;
        this.rightSide = rightSide;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getBooleanValue() {
        //This should cover all cases
        return (leftSide.getValue().equals(rightSide.getValue()));
    }
}

