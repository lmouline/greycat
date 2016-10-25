package org.mwg.mlx.algorithm.ruleinference.nodes;

/**
 * Created by andrey.boytsov on 25/10/2016.
 */
public abstract class DoubleNode implements ConditionGraphNode{
    /**
     * {@inheritDoc}
     */
    @Override
    public String getValue() {
        return ""+getDoubleValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getBooleanValue() {
        return getDoubleValue() > 0;
    }
}
