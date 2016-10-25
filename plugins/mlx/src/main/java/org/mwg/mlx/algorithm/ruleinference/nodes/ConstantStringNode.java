package org.mwg.mlx.algorithm.ruleinference.nodes;

/**
 * Created by andrey.boytsov on 25/10/2016.
 */
public class ConstantStringNode implements ConditionGraphNode {
    private final String value;

    /**
     * @param value Node value
     */
    public ConstantStringNode(String value) {
        this.value = value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getValue() {
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getBooleanValue() {
        return Boolean.parseBoolean(this.value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getDoubleValue() {
        return Double.parseDouble(this.value);
    }
}
