package org.mwg.struct;

import org.junit.Test;
import org.mwg.Graph;
import org.mwg.GraphBuilder;
import org.mwg.Type;
import org.mwg.struct.action.NTreeInsertTo;
import org.mwg.struct.action.NTreeNearestN;
import org.mwg.struct.distance.Distances;
import org.mwg.struct.tree.KDTree;
import org.mwg.task.Task;

import static org.mwg.task.Actions.*;

public class GeoIndexTaskTest {

    @Test
    public void test() {
        Graph g = new GraphBuilder().withMemorySize(100000).withPlugin(new StructPlugin()).build();
        g.connect(result -> {

            Task createGeoIndex = newTypedNode(KDTree.NAME)
                    .setProperty(KDTree.DISTANCE, Type.INT, Distances.GEODISTANCE + "")
                    .setProperty(KDTree.FROM, Type.STRING, "lat,long")
                    .asGlobalVar("geoIndex");

            Task createTenPoints = loop("0", "9", newNode().setProperty("lat", Type.DOUBLE, "1.{{i}}").setProperty("long", Type.DOUBLE, "2.{{i}}").addToGlobalVar("points"));

            newTask()
                    .subTask(createGeoIndex)
                    .subTask(createTenPoints)
                    .fromVar("points")
                    .action(NTreeInsertTo.NAME, "geoIndex")
                    .fromVar("geoIndex")
                    .action(NTreeNearestN.NAME, "1.5,2.5,3")
                    .print("{{result}}")
                    .execute(g, null);


            /*
            newNode()
                    .setProperty("name", Type.STRING, "toIndex")
                    .asVar("toIndex").newTypedNode(KDTree.NAME);
            //.action(KDT)


            NTree kdTree = (NTree) g.newTypedNode(0, 0, KDTree.NAME);
            kdTree.insert(new double[]{0.3, 0.5}, null, insertRes -> {
                System.out.println(insertRes);
            });

            System.out.println(kdTree);
*/


        });

    }

}
