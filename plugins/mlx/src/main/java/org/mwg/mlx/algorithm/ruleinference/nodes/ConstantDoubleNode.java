package org.mwg.mlx.algorithm.ruleinference.nodes;

/**
 * Created by andrey.boytsov on 24/10/2016.
 */
public class ConstantDoubleNode extends DoubleNode {
    private final double val;

    public ConstantDoubleNode(double value) {
        this.val = value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getDoubleValue() {
        return val;
    }
}
