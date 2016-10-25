package org.mwg.mlx.algorithm.ruleinference.nodes;

/**
 * Created by andrey.boytsov on 24/10/2016.
 */
public class OrNode extends BooleanNode {
    private final ConditionGraphNode parentNodes[];

    public OrNode(ConditionGraphNode previousNodes[]) {
        this.parentNodes = previousNodes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getBooleanValue() {
        for (ConditionGraphNode node : parentNodes) {
            if (node.getBooleanValue()) {
                return true;
            }
        }
        return false;
    }
}
