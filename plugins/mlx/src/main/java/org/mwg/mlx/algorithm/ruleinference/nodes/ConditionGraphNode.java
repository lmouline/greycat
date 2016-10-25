package org.mwg.mlx.algorithm.ruleinference.nodes;

/**
 * For example, it can be a value of the node, or some constant, or something more tricky (e.g. for "is a workday").
 */
public interface ConditionGraphNode{
    /**
     * @return Value of the node. Can be numeric, boolean, or any other depending on the context.
     */
    String getValue();

    /**
     * @return Value of the node, cast to boolean (&gt; 0 is true for double; &lt;= 0 - false)
     * @throws IllegalStateException If the value cannot be cast to boolean
     */
    boolean getBooleanValue();

    /**
     * @return Value of the node, cast to double (+1/-1 for true/false)
     * @throws IllegalStateException If the value cannot be cast to double
     */
    double getDoubleValue();
}

