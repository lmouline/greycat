package org.mwg.struct;

import org.junit.Test;
import org.mwg.Graph;
import org.mwg.GraphBuilder;
import org.mwg.struct.tree.KDTree;

public class KDTreeTaskTest {

    @Test
    public void test() {
        Graph g = new GraphBuilder().withMemorySize(100000).withPlugin(new StructPlugin()).build();
        g.connect(result -> {

            NTree kdTree = (NTree) g.newTypedNode(0, 0, KDTree.NAME);
            kdTree.insert(new double[]{0.3,0.5}, null, insertRes -> {
                System.out.println(insertRes);
            });

            System.out.println(kdTree);


        });

    }

}
