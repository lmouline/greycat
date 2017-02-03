package org.mwg.ml;

import org.mwg.Graph;
import org.mwg.Node;
import org.mwg.Type;
import org.mwg.ml.algorithm.anomalydetector.InterquartileRangeOutlierDetectorNode;
import org.mwg.ml.algorithm.profiling.GaussianMixtureNode;
import org.mwg.ml.algorithm.profiling.GaussianNode;
import org.mwg.ml.algorithm.profiling.GaussianSlotNode;
//import org.mwg.ml.algorithm.profiling.GaussianTreeNode;
import org.mwg.ml.algorithm.regression.LiveLinearRegressionNode;
import org.mwg.ml.algorithm.regression.PolynomialNode;
import org.mwg.ml.algorithm.regression.actions.ReadContinuous;
import org.mwg.ml.algorithm.regression.actions.SetContinuous;
import org.mwg.ml.neuralnet.FlatNeuralNode;
import org.mwg.ml.neuralnet.NeuralNode;
import org.mwg.ml.neuralnet.NeuralNodeEmpty;
import org.mwg.plugin.ActionFactory;
import org.mwg.plugin.NodeFactory;
import org.mwg.structure.StructurePlugin;
import org.mwg.task.Action;
import org.mwg.task.TaskActionFactory;

public class MLPlugin extends StructurePlugin {

    @Override
    public void start(Graph graph) {
        super.start(graph);
        graph.actionRegistry()
                .declaration(ReadContinuous.NAME)
                .setParams(Type.STRING)
                .setFactory(new ActionFactory() {
                    @Override
                    public Action create(Object[] params) {
                        return new ReadContinuous((String) params[0]);
                    }
                });
        graph.actionRegistry()
                .declaration(SetContinuous.NAME)
                .setParams(Type.STRING, Type.STRING)
                .setFactory(new ActionFactory() {
                    @Override
                    public Action create(Object[] params) {
                        return new SetContinuous((String) params[0], (String) params[1]);
                    }
                });

        graph.nodeRegistry()
                .declaration(PolynomialNode.NAME)
                .setFactory(new NodeFactory() {
                    @Override
                    public Node create(long world, long time, long id, Graph graph) {
                        return new PolynomialNode(world, time, id, graph);
                    }
                });
        graph.nodeRegistry()
                .declaration(FlatNeuralNode.NAME)
                .setFactory(new NodeFactory() {
                    @Override
                    public Node create(long world, long time, long id, Graph graph) {
                        return new FlatNeuralNode(world, time, id, graph);
                    }
                });
        graph.nodeRegistry()
                .declaration(GaussianNode.NAME)
                .setFactory(new NodeFactory() {
                    @Override
                    public Node create(long world, long time, long id, Graph graph) {
                        return new GaussianNode(world, time, id, graph);
                    }
                });
        graph.nodeRegistry()
                .declaration(GaussianSlotNode.NAME)
                .setFactory(new NodeFactory() {
                    @Override
                    public Node create(long world, long time, long id, Graph graph) {
                        return new GaussianSlotNode(world, time, id, graph);
                    }
                });
        graph.nodeRegistry()
                .declaration(GaussianMixtureNode.NAME)
                .setFactory(new NodeFactory() {
                    @Override
                    public Node create(long world, long time, long id, Graph graph) {
                        return new GaussianMixtureNode(world, time, id, graph);
                    }
                });
        graph.nodeRegistry()
                .declaration(LiveLinearRegressionNode.NAME)
                .setFactory(new NodeFactory() {
                    @Override
                    public Node create(long world, long time, long id, Graph graph) {
                        return new LiveLinearRegressionNode(world, time, id, graph);
                    }
                });
        graph.nodeRegistry()
                .declaration(InterquartileRangeOutlierDetectorNode.NAME)
                .setFactory(new NodeFactory() {
                    @Override
                    public Node create(long world, long time, long id, Graph graph) {
                        return new InterquartileRangeOutlierDetectorNode(world, time, id, graph);
                    }
                });
        graph.nodeRegistry()
                .declaration(NeuralNode.NAME)
                .setFactory(new NodeFactory() {
                    @Override
                    public Node create(long world, long time, long id, Graph graph) {
                        return new NeuralNode(world, time, id, graph);
                    }
                });
        graph.nodeRegistry()
                .declaration(NeuralNodeEmpty.NAME)
                .setFactory(new NodeFactory() {
                    @Override
                    public Node create(long world, long time, long id, Graph graph) {
                        return new NeuralNodeEmpty(world, time, id, graph);
                    }
                });
    }
}
