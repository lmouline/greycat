package org.mwg.ml;

import org.mwg.Graph;
import org.mwg.Node;
import org.mwg.ml.algorithm.anomalydetector.InterquartileRangeOutlierDetectorNode;
import org.mwg.ml.algorithm.profiling.GaussianMixtureNode;
import org.mwg.ml.algorithm.profiling.GaussianNode;
import org.mwg.ml.algorithm.profiling.GaussianSlotNode;
import org.mwg.ml.algorithm.profiling.GaussianTreeNode;
import org.mwg.ml.algorithm.regression.LiveLinearRegressionNode;
import org.mwg.ml.algorithm.regression.PolynomialNode;
import org.mwg.ml.common.structure.KDNode;
import org.mwg.plugin.AbstractPlugin;
import org.mwg.plugin.NodeFactory;

public class MLPlugin extends AbstractPlugin {

    public MLPlugin() {
        super();
        //PolynomialNode
        declareNodeType(PolynomialNode.NAME, new NodeFactory() {
            @Override
            public Node create(long world, long time, long id, Graph graph) {
                return new PolynomialNode(world, time, id, graph);
            }
        });
        //GaussianSlot
        declareNodeType(GaussianSlotNode.NAME, new NodeFactory() {
            @Override
            public Node create(long world, long time, long id, Graph graph) {
                return new GaussianSlotNode(world, time, id, graph);
            }
        });
        //GaussianMixtureNode
        declareNodeType(GaussianMixtureNode.NAME, new NodeFactory() {
            @Override
            public Node create(long world, long time, long id, Graph graph) {
                return new GaussianMixtureNode(world, time, id, graph);
            }
        });
        //LiveRegressionNode
        declareNodeType(LiveLinearRegressionNode.NAME, new NodeFactory() {
            @Override
            public Node create(long world, long time, long id, Graph graph) {
                return new LiveLinearRegressionNode(world, time, id, graph);
            }
        });
        //InterquartileRangeOutlierDetectorNode
        declareNodeType(InterquartileRangeOutlierDetectorNode.NAME, new NodeFactory() {
            @Override
            public Node create(long world, long time, long id, Graph graph) {
                return new InterquartileRangeOutlierDetectorNode(world, time, id, graph);
            }
        });

        declareNodeType(KDNode.NAME, new NodeFactory() {
            @Override
            public Node create(long world, long time, long id, Graph graph) {
                return new KDNode(world, time, id, graph);
            }
        });

        declareNodeType(GaussianTreeNode.NAME, new NodeFactory() {
            @Override
            public Node create(long world, long time, long id, Graph graph) {
                return new GaussianTreeNode(world, time, id, graph);
            }
        });

        declareNodeType(GaussianNode.NAME, new NodeFactory() {
            @Override
            public Node create(long world, long time, long id, Graph graph) {
                return new GaussianNode(world, time, id, graph);
            }
        });

    }
}
