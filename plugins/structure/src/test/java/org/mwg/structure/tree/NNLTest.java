package org.mwg.structure.tree;

import org.mwg.*;
import org.mwg.struct.EGraph;
import org.mwg.struct.ENode;
import org.mwg.structure.util.NearestNeighborList;
import org.mwg.structure.util.VolatileResult;

import java.util.HashMap;
import java.util.Random;

/**
 * @ignore ts
 */
public class NNLTest {
    public static void main(String[] arg) {

        Graph graph = GraphBuilder.newBuilder().build();
        graph.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean result) {
                Random random = new Random(102365);
                int capacity = 5;
                int test = 30;
                int count=100;


                Node kmf = graph.newNode(0, 0);
                EGraph egraph = (EGraph) kmf.getOrCreate("test", Type.EGRAPH);
                ENode root = egraph.newNode();
                egraph.setRoot(root);

                NearestNeighborList nnl = new NearestNeighborList(capacity);
                VolatileResult vr = new VolatileResult(root, capacity);

                HashMap<Long, double[]> dictionary = new HashMap<Long, double[]>();

                int countk=0;

                for (int i = 0; i < test; i++) {
                    double[] key = new double[3];
                    long id;
                    double distance;

                    for (int j = 0; j < key.length; j++) {
                        key[j] = countk++;
                    }
                    id = count++;
                    distance = random.nextDouble();

                    dictionary.put(id, key);
                    nnl.insert(id, distance);
                    vr.insert(key, id, distance);

                    int y=0;
                }


                vr.sort(false);
                int x=0;


            }
        });

    }

}
