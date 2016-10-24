package org.mwg.mlx.algorithm.ruleinference.nodes;

import org.mwg.mlx.algorithm.ruleinference.RuleNode;

/**
 * Created by andrey.boytsov on 24/10/2016.
 */
public class AndNode implements ConditionGraphNode {
    private final ConditionGraphNode parentNodes[];

    public AndNode(ConditionGraphNode previousNodes[]) {
        this.parentNodes = previousNodes;
    }

    @Override
    public double getValue() {
        for (ConditionGraphNode node : parentNodes){
            if (node.getValue() <= 0){
                return -1;
            }
        }
        return 1;
    }
}
