package org.mwg.mlx.algorithm.ruleinference.nodes;

/**
 * Created by andrey.boytsov on 25/10/2016.
 */
public class ConstantBooleanNode extends BooleanNode{
    private final boolean value;

    /**
     * @param value Node value
     */
    public ConstantBooleanNode(boolean value) {
        this.value = value;
    }

    /**
     * @param value Node value ("true" for true, rest for false, case insensitive)
     */
    public ConstantBooleanNode(String value) {
        this.value = Boolean.parseBoolean(value.toLowerCase());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getBooleanValue() {
        return value;
    }
}
