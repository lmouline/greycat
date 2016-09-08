package org.mwg.struct;

import org.junit.Test;
import org.mwg.Graph;
import org.mwg.GraphBuilder;
import org.mwg.Type;
import org.mwg.struct.action.NTreeInsertTo;
import org.mwg.struct.action.NTreeNearestN;
import org.mwg.struct.action.NTreeNearestNWithinRadius;
import org.mwg.struct.action.NTreeNearestWithinRadius;
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

            Task createTenPoints = loop("0", "9", newNode().setProperty("lat", Type.DOUBLE, "49.{{i}}").setProperty("long", Type.DOUBLE, "6.{{i}}").addToGlobalVar("points"));

            newTask()
                    .subTask(createGeoIndex)
                    .subTask(createTenPoints)
                    .fromVar("points")
                    .action(NTreeInsertTo.NAME, "geoIndex")
                    .fromVar("geoIndex")
                    .action(NTreeNearestN.NAME, "49.6116,6.1319,3") //lat,long,nb
                    .print("{{result}}")
                    .fromVar("geoIndex")
                    .action(NTreeNearestWithinRadius.NAME, "49.6116,6.1319,100000") //lat,long,meters
                    .print("{{result}}")
                    .fromVar("geoIndex")
                    .action(NTreeNearestNWithinRadius.NAME, "49.6116,6.1319,2,100000") //lat,long,nb,meters
                    .print("{{result}}")
                    .execute(g, null);
            
        });

    }

}
