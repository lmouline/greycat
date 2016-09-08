package org.mwg.struct;

import org.mwg.Graph;
import org.mwg.Node;
import org.mwg.plugin.AbstractPlugin;
import org.mwg.plugin.NodeFactory;
import org.mwg.struct.action.NTreeInsertTo;
import org.mwg.struct.action.NTreeNearestN;
import org.mwg.struct.action.TraverseById;
import org.mwg.struct.tree.KDTree;
import org.mwg.task.TaskAction;
import org.mwg.task.TaskActionFactory;

public class StructPlugin extends AbstractPlugin {

    public StructPlugin() {
        /*
        declareNodeType(RBTree.NAME, new NodeFactory() {
            @Override
            public Node create(long world, long time, long id, Graph graph) {
                return new RBTree(world,time,id,graph);
            }
        });
        declareNodeType(RBTreeNode.NAME, new NodeFactory() {
            @Override
            public Node create(long world, long time, long id, Graph graph) {
                return new RBTreeNode(world,time,id,graph);
            }
        });*/

        declareNodeType(KDTree.NAME, new NodeFactory() {
            @Override
            public Node create(long world, long time, long id, Graph graph) {
                return new KDTree(world, time, id, graph);
            }
        });

        /* Custom actions */
        declareTaskAction(NTreeInsertTo.NAME, new TaskActionFactory() {
            @Override
            public TaskAction create(String[] params) {
                if (params.length != 1) {
                    throw new RuntimeException("Bad param number!");
                }
                return new NTreeInsertTo(params[0]);
            }
        });
        declareTaskAction(TraverseById.NAME, new TaskActionFactory() {
            @Override
            public TaskAction create(String[] params) {
                if (params.length != 1) {
                    throw new RuntimeException("Bad param number!");
                }
                return new TraverseById(params[0]);
            }
        });


        /** TODO */
        declareTaskAction(NTreeNearestN.NAME, new TaskActionFactory() {
            @Override
            public TaskAction create(String[] params) {
                if (params.length < 2) {
                    throw new RuntimeException("Bad param number!");
                }
                int n = Integer.parseInt(params[params.length - 1]);
                double[] key = new double[params.length - 1];
                for (int i = 0; i < params.length - 1; i++) {
                    key[i] = Double.parseDouble(params[i]);
                }
                return new NTreeNearestN(key, n);
            }
        });
        /*
        declareTaskAction(TraverseById.NAME, new TaskActionFactory() {
            @Override
            public TaskAction create(String[] params) {
                if (params.length != 1) {
                    throw new RuntimeException("Bad param number!");
                }
                return new TraverseById(params[0]);
            }
        });
        declareTaskAction(TraverseById.NAME, new TaskActionFactory() {
            @Override
            public TaskAction create(String[] params) {
                if (params.length != 1) {
                    throw new RuntimeException("Bad param number!");
                }
                return new TraverseById(params[0]);
            }
        });*/


    }
}
