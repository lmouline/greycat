package greycat.structure.trees;

import greycat.*;
import org.junit.Test;
import org.mwg.*;
import greycat.struct.EGraph;
import greycat.struct.ENode;
import greycat.structure.StructurePlugin;
import greycat.structure.util.VolatileResult;

import java.util.Random;


public class TestSort {

    @Test
    public void testsort() {
        final Graph graph = new GraphBuilder()
                .withPlugin(new StructurePlugin())
                .withMemorySize(10000)
                .build();
        graph.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean result) {
                int max = 20;
                int maxinsert = 200;
                Random random = new Random();
                random.setSeed(1234);


                for (int i = 0; i < max; i++) {
                    int capacity = i + 50;
                    Node kmf = graph.newNode(0, 0);
                    EGraph egraph = (EGraph) kmf.getOrCreate("test", Type.EGRAPH);
                    ENode root = egraph.newNode();
                    egraph.setRoot(root);
                    VolatileResult vr = new VolatileResult(root, capacity);

                    for (int k = 0; k < maxinsert; k++) {
                        vr.insert(new double[]{k}, k, random.nextDouble());
                    }
                    vr.sort(true);

                    for (int k = 0; k < vr.size() - 1; k++) {
                        assert (vr.distance(k) < vr.distance(k + 1));
                    }

                    vr.sort(false);
                    for (int k = 0; k < vr.size() - 1; k++) {
                        assert (vr.distance(k) > vr.distance(k + 1));
                    }
                    kmf.free();
                    graph.save(null);
                }

            }
        });


    }
}
