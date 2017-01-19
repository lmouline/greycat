package org.mwg.structure;

import org.mwg.Graph;
import org.mwg.Node;
import org.mwg.Type;
import org.mwg.plugin.ActionFactory;
import org.mwg.plugin.NodeFactory;
import org.mwg.plugin.Plugin;
import org.mwg.structure.action.NTreeNearestNWithinRadius;
import org.mwg.structure.tree.KDTreeOld;
import org.mwg.structure.tree.NDTree;
import org.mwg.structure.tree.KDTree;
import org.mwg.task.Action;

public class StructurePlugin implements Plugin {

    @Override
    public void start(Graph graph) {
        graph.actionRegistry()
                .declaration(NTreeNearestNWithinRadius.NAME)
                .setParams(Type.INT, Type.DOUBLE, Type.DOUBLE_ARRAY)
                .setFactory(new ActionFactory() {
                    @Override
                    public Action create(Object[] params) {
                        return new NTreeNearestNWithinRadius((int) params[0], (double) params[1], (double[]) params[2]);
                    }
                });

        graph.nodeRegistry().declaration(KDTreeOld.NAME).setFactory(new NodeFactory() {
            @Override
            public Node create(long world, long time, long id, Graph graph) {
                return new KDTreeOld(world, time, id, graph);
            }
        });
        graph.nodeRegistry().declaration(NDTree.NAME).setFactory(new NodeFactory() {
            @Override
            public Node create(long world, long time, long id, Graph graph) {
                return new NDTree(world, time, id, graph);
            }
        });
    }

    @Override
    public void stop() {

    }


}
