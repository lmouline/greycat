package org.mwg.mlx.algorithm.ruleinference.nodes;

/**
 * Created by andrey.boytsov on 25/10/2016.
 */
public abstract class BooleanNode implements ConditionGraphNode{
    public static final String TRUE_STR = ""+Boolean.TRUE;
    public static final String FALSE_STR = ""+Boolean.FALSE;

    public static final double TRUE_DOUBLE = +1;
    public static final double FALSE_DOUBLE = -1;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getValue() {
        return getBooleanValue() ? TRUE_STR : FALSE_STR;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getDoubleValue() {
        return getBooleanValue() ? TRUE_DOUBLE : FALSE_DOUBLE;
    }
}
