package org.mwg.struct;

import org.junit.Assert;
import org.junit.Test;
import org.mwg.Callback;
import org.mwg.Graph;
import org.mwg.GraphBuilder;
import org.mwg.Type;
import org.mwg.struct.action.NTreeInsertTo;
import org.mwg.struct.action.NTreeNearestN;
import org.mwg.struct.action.NTreeNearestNWithinRadius;
import org.mwg.struct.action.NTreeNearestWithinRadius;
import org.mwg.struct.distance.Distances;
import org.mwg.struct.tree.KDTree;
import org.mwg.task.Action;
import org.mwg.task.Task;
import org.mwg.task.TaskContext;

import static org.mwg.task.Actions.*;

public class GeoIndexTaskTest {

    @Test
    public void test() {
        Graph g = new GraphBuilder().withMemorySize(100000).withPlugin(new StructPlugin()).build();
        g.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean result) {

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
                        .then(new Action() {
                            @Override
                            public void eval(TaskContext context) {
                                Assert.assertEquals(context.result().toString(),"[{\"world\":0,\"time\":0,\"id\":7,\"lat\":49.5,\"long\":6.5},{\"world\":0,\"time\":0,\"id\":6,\"lat\":49.4,\"long\":6.4},{\"world\":0,\"time\":0,\"id\":8,\"lat\":49.6,\"long\":6.6}]");
                                context.continueTask();
                            }
                        })
                        .fromVar("geoIndex")
                        .action(NTreeNearestWithinRadius.NAME, "49.6116,6.1319,100000") //lat,long,meters
                        .then(new Action() {
                            @Override
                            public void eval(TaskContext context) {
                                Assert.assertEquals(context.result().toString(),"[{\"world\":0,\"time\":0,\"id\":7,\"lat\":49.5,\"long\":6.5},{\"world\":0,\"time\":0,\"id\":6,\"lat\":49.4,\"long\":6.4},{\"world\":0,\"time\":0,\"id\":8,\"lat\":49.6,\"long\":6.6},{\"world\":0,\"time\":0,\"id\":5,\"lat\":49.3,\"long\":6.3},{\"world\":0,\"time\":0,\"id\":9,\"lat\":49.7,\"long\":6.7},{\"world\":0,\"time\":0,\"id\":4,\"lat\":49.2,\"long\":6.2},{\"world\":0,\"time\":0,\"id\":10,\"lat\":49.8,\"long\":6.8},{\"world\":0,\"time\":0,\"id\":3,\"lat\":49.1,\"long\":6.1},{\"world\":0,\"time\":0,\"id\":11,\"lat\":49.9,\"long\":6.9},{\"world\":0,\"time\":0,\"id\":2,\"lat\":49.0,\"long\":6.0}]");
                                context.continueTask();
                            }
                        })
                        .fromVar("geoIndex")
                        .action(NTreeNearestNWithinRadius.NAME, "49.6116,6.1319,2,100000") //lat,long,nb,meters
                        .then(new Action() {
                            @Override
                            public void eval(TaskContext context) {
                                Assert.assertEquals(context.result().toString(),"[{\"world\":0,\"time\":0,\"id\":7,\"lat\":49.5,\"long\":6.5},{\"world\":0,\"time\":0,\"id\":6,\"lat\":49.4,\"long\":6.4}]");
                                context.continueTask();
                            }
                        })
                        .execute(g, null);
            }
        });

    }

}
