package org.mwg.mlx.algorithm.ruleinference.nodes;

/**
 * For example, it can be a value of the node, or some constant, or something more tricky (e.g. for "is a workday").
 */
public interface ConditionGraphNode{
    /**
     * @return Value of the node. Can be numeric or boolean (negative - false; non-negative - true).
     */
    double getValue();
}

