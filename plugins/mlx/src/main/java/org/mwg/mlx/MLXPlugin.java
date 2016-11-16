package org.mwg.mlx;

import org.mwg.Graph;
import org.mwg.Node;
import org.mwg.ml.MLPlugin;
import org.mwg.mlx.algorithm.classifier.BatchDecisionTreeNode;
import org.mwg.mlx.algorithm.classifier.GaussianClassifierNode;
import org.mwg.mlx.algorithm.classifier.GaussianNaiveBayesianNode;
import org.mwg.mlx.algorithm.classifier.LogisticRegressionClassifierNode;
import org.mwg.mlx.algorithm.regression.*;
import org.mwg.plugin.NodeFactory;

public class MLXPlugin extends MLPlugin {

    public MLXPlugin() {
        super();
        declareNodeType(LinearRegressionWithPeriodicityNode.NAME, new NodeFactory() {
            @Override
            public Node create(long world, long time, long id, Graph graph) {
                return new LinearRegressionWithPeriodicityNode(world, time, id, graph);
            }
        });
        declareNodeType(LinearRegressionNode.NAME, new NodeFactory() {
            @Override
            public Node create(long world, long time, long id, Graph graph) {
                return new LinearRegressionNode(world, time, id, graph);
            }
        });
        declareNodeType(LinearRegressionBatchGDNode.NAME, new NodeFactory() {
            @Override
            public Node create(long world, long time, long id, Graph graph) {
                return new LinearRegressionBatchGDNode(world, time, id, graph);
            }
        });
        declareNodeType(LinearRegressionSGDNode.NAME, new NodeFactory() {
            @Override
            public Node create(long world, long time, long id, Graph graph) {
                return new LinearRegressionSGDNode(world, time, id, graph);
            }
        });
        declareNodeType(GaussianNaiveBayesianNode.NAME, new NodeFactory() {
            @Override
            public Node create(long world, long time, long id, Graph graph) {
                return new GaussianNaiveBayesianNode(world, time, id, graph);
            }
        });
        declareNodeType(LogisticRegressionClassifierNode.NAME, new NodeFactory() {
            @Override
            public Node create(long world, long time, long id, Graph graph) {
                return new LogisticRegressionClassifierNode(world, time, id, graph);
            }
        });
        //BatchDecisionTreeNode
        declareNodeType(BatchDecisionTreeNode.NAME, new NodeFactory() {
            @Override
            public Node create(long world, long time, long id, Graph graph) {
                return new BatchDecisionTreeNode(world, time, id, graph);
            }
        });
        declareNodeType(GaussianClassifierNode.NAME, new NodeFactory() {
            @Override
            public Node create(long world, long time, long id, Graph graph) {
                return new GaussianClassifierNode(world, time, id, graph);
            }
        });
        declareNodeType(AutoregressionBasedPeriodicityDetector.NAME, new NodeFactory() {
            @Override
            public Node create(long world, long time, long id, Graph graph) {
                return new AutoregressionBasedPeriodicityDetector(world, time, id, graph);
            }
        });
    }

}
