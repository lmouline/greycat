package org.mwg.mlx.algorithm.ruleinference.nodes;

/**
 * Created by andrey.boytsov on 24/10/2016.
 */
public class ConstantNode implements ConditionGraphNode {
    private final double val;

    public ConstantNode(double value) {
        this.val = value;
    }

    @Override
    public double getValue() {
        return val;
    }
}
