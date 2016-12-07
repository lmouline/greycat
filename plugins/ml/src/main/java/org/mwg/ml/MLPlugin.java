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
import org.mwg.ml.algorithm.regression.actions.ActionGetContinuous;
import org.mwg.ml.algorithm.regression.actions.ActionSetContinuous;
import org.mwg.ml.neuralnet.FlatNeuralNode;
import org.mwg.ml.neuralnet.NeuralNode;
import org.mwg.ml.neuralnet.NeuralNodeEmpty;
import org.mwg.plugin.NodeFactory;
import org.mwg.structure.StructurePlugin;
import org.mwg.task.Action;
import org.mwg.task.TaskActionFactory;

public class MLPlugin extends StructurePlugin {

    public MLPlugin() {
        super();
        //PolynomialNode
        declareNodeType(PolynomialNode.NAME, new NodeFactory() {
            @Override
            public Node create(long world, long time, long id, Graph graph) {
                return new PolynomialNode(world, time, id, graph);
            }
        });

        declareNodeType(FlatNeuralNode.NAME, new NodeFactory() {
            @Override
            public Node create(long world, long time, long id, Graph graph) {
                return new FlatNeuralNode(world, time, id, graph);
            }
        });

        //A simple Gaussian Node
        declareNodeType(GaussianNode.NAME, new NodeFactory() {
            @Override
            public Node create(long world, long time, long id, Graph graph) {
                return new GaussianNode(world, time, id, graph);
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

        declareNodeType(NeuralNode.NAME, new NodeFactory() {
            @Override
            public Node create(long world, long time, long id, Graph graph) {
                return new NeuralNode(world, time, id, graph);
            }
        });

        declareNodeType(NeuralNodeEmpty.NAME, new NodeFactory() {
            @Override
            public Node create(long world, long time, long id, Graph graph) {
                return new NeuralNode(world, time, id, graph);
            }
        });

        declareNodeType(GaussianTreeNode.NAME, new NodeFactory() {
            @Override
            public Node create(long world, long time, long id, Graph graph) {
                return new GaussianTreeNode(world, time, id, graph);
            }
        });


        declareTaskAction(ActionSetContinuous.NAME, new TaskActionFactory() {
            @Override
            public Action create(String[] params) {
                if(params.length!=2){
                    throw new RuntimeException("set continuous requires 2 parameters");
                }
                return new ActionSetContinuous(params[0],params[1]);
            }
        });

        declareTaskAction(ActionGetContinuous.NAME, new TaskActionFactory() {
            @Override
            public Action create(String[] params) {
                if(params.length!=1){
                    throw new RuntimeException("set continuous takes 1 parameter");
                }
                return new ActionGetContinuous(params[0]);
            }
        });


    }
}
