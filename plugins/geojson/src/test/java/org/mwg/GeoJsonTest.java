package org.mwg;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import org.mwg.ml.MLPlugin;
import org.mwg.ml.common.structure.KDTree;
import org.mwg.plugin.geojson.GeoJsonPlugin;
import org.mwg.task.*;
import org.mwg.utility.HashHelper;


import java.util.Iterator;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.mwg.plugin.geojson.GeoJsonActions.loadJson;
import static org.mwg.plugin.geojson.GeoJsonActions.newNodeFromJson;
import static org.mwg.task.Actions.*;

public class GeoJsonTest {

    private final static String _baseParam = "?apiKey=9c84c41dfc20a1539e3cb1239efa65889b6341e4";
    private final static String _stationsListAddress = "https://api.jcdecaux.com/vls/v1/stations";
    private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();


    //@Test
    public void baseTest() {


        //API KEY:          9c84c41dfc20a1539e3cb1239efa65889b6341e4
        // GetStations:     https://api.jcdecaux.com/vls/v1/stations
        // Contract information GET https://api.jcdecaux.com/vls/v1/stations/{station_number}?contract={contract_name} HTTP/1.1


        final Graph g = new GraphBuilder().withPlugin(new GeoJsonPlugin()).withPlugin(new MLPlugin()).build();
        g.connect(connectionResult -> {

            WSServer graphServer = new WSServer(g, 8050);
            graphServer.start();


            final Task t = loadJson(_stationsListAddress + _baseParam)
                    .isolate(newTypedNode(KDTree.NAME).asVar("tree").indexNode("positionsTree", ""))
                    .foreachPar(
                            newNodeFromJson()
                                    .indexNode("stations", "contract_name, name")
                                    .asVar("node")
                                    .traverse("position")
                                    .then(new Action() {
                                        @Override
                                        public void eval(TaskContext context) {
                                            KDTree tree = (KDTree) context.variable("tree").get(0);
                                            Node n = context.resultAsNodes().get(0);
                                                tree.insert(
                                                        new double[]{(double) n.get("lat"), (double) n.get("lng")},
                                                        (Node) context.variable("node").get(0),
                                                        null);
                                            context.continueTask();
                                        }
                                    }).fromVar("node"))
                    .print("{{result}}");
            t.execute(g, null);

            executor.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    update(g);
                }
            }, 30,30, TimeUnit.SECONDS);
        });
    }

    private void update(Graph graph) {
        System.out.println("Updating");
        final Task update = loadJson(_stationsListAddress + _baseParam)
                .isolate(fromIndexAll("positionsTree").asVar("tree"))
                .jump(System.currentTimeMillis()+"")
                .foreachPar(
                        asVar("jsonObject")
                        .fromIndex("stations", "contract_name={{contract_name}};name={{name}}")
                        .ifThen(new TaskFunctionConditional() {
                            @Override
                            public boolean eval(TaskContext context) {
                                return context.result().size() == 0;
                            }
                        }, newNode())
                        .then(new Action() {
                            @Override
                            public void eval(TaskContext context) {
                                Node n = context.resultAsNodes().get(0);
                                JsonObject obj = (JsonObject)context.variable("jsonObject").get(0);
                                Iterator<JsonObject.Member> mIt = obj.iterator();
                                while(mIt.hasNext()) {
                                    JsonObject.Member m = mIt.next();
                                    JsonValue value = m.getValue();
                                    if (value.isString()) {
                                        n.set(m.getName(), value.asString());
                                    } else if (value.isBoolean()) {
                                        n.set(m.getName(), value.asBoolean());
                                    } else if (value.isNumber()) {
                                        try {
                                            n.set(m.getName(), value.asInt());
                                        } catch (Exception e) {
                                            try {
                                                n.set(m.getName(), value.asLong());
                                            } catch (Exception e2) {
                                                try {
                                                    n.set(m.getName(), value.asFloat());
                                                } catch (Exception e3) {
                                                    n.set(m.getName(), value.asDouble());
                                                }
                                            }
                                        }
                                    }
                                }
                                context.continueTask();
                            }
                        })).print("Update done.");
        update.execute(graph, null);

    }

    public static void main(String[] args) {
        GeoJsonTest test = new GeoJsonTest();
        test.baseTest();
    }


}
