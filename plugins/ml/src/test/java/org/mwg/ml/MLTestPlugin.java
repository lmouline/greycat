package org.mwg.ml;

import org.mwg.Graph;
import org.mwg.Node;
import org.mwg.ml.common.structure.KDTreeAsync;
import org.mwg.plugin.AbstractPlugin;
import org.mwg.plugin.NodeFactory;

/**
 * Created by assaad on 04/08/16.
 */
public class MLTestPlugin extends AbstractPlugin {

    public MLTestPlugin() {
        super();
        //PolynomialNode
        declareNodeType(KDTreeAsync.NAME, new NodeFactory() {
            @Override
            public Node create(long world, long time, long id, Graph graph) {
                return new KDTreeAsync(world, time, id, graph);
            }
        });
    }
}