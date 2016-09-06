package org.mwg;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import org.mwg.core.task.CoreTask;
import org.mwg.ml.MLPlugin;
import org.mwg.ml.common.structure.KDTree;
import org.mwg.plugin.geojson.ActionNewNodeFromJson;
import org.mwg.plugin.geojson.GeoJsonPlugin;
import org.mwg.task.*;
import org.mwg.utility.HashHelper;
import org.mwg.utility.VerboseHookFactory;
import org.mwg.utility.VerbosePlugin;


import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.mwg.plugin.geojson.GeoJsonActions.*;
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

        final Graph g = new GraphBuilder().withPlugin(new GeoJsonPlugin()).withPlugin(new MLPlugin()).withPlugin(new VerbosePlugin()).build();
        g.connect(connectionResult -> {

            WSServer graphServer = new WSServer(g, 8050);
            graphServer.start();

            while(true) {
                update(g);
                try {
                    Thread.sleep(30*1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            /*
            executor.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    try {

                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                }
            }, 0, 30, TimeUnit.SECONDS);
            */
        });
    }

    private void update(Graph graph) {

        System.out.println("Updating");


        final Task update =
                fromIndexAll("positionsTree")
                        .hook(new VerboseHookFactory())
                        .ifThen(new TaskFunctionConditional() {
                            @Override
                            public boolean eval(TaskContext context) {
                                return context.result().size() == 0;
                            }
                        }, setTime("" + Constants.BEGINNING_OF_TIME).newTypedNode(KDTree.NAME).print("Positions Tree created !").indexNode("positionsTree", ""))
                        .asVar("tree")
                        .action(LOADJSON, _stationsListAddress + _baseParam)
                        //.hook(new VerboseHookFactory())
                        .foreach(
                                setTime(System.currentTimeMillis() + "")
                                .asVar("jsonObject")
                                        .then(new Action() {
                                            @Override
                                            public void eval(TaskContext context) {
                                                JsonObject obj = (JsonObject) context.result().get(0);

                                                Query q = graph.newQuery();
                                                q.setTime(context.time());
                                                q.setWorld(context.world());
                                                q.setIndexName("stations");
                                                q.add("contract_name", obj.get("contract_name").asString());
                                                q.add("name", obj.get("name").asString());

                                                graph.findByQuery(q, new Callback<Node[]>() {
                                                    @Override
                                                    public void on(Node[] result) {
                                                        TaskResult<Node> newRes = context.newResult();
                                                        if (result.length > 0) {
                                                            newRes.add(result[0]);
                                                        }
                                                        context.continueWith(newRes);
                                                    }
                                                });
                                            }
                                        })
                                        .ifThen(new TaskFunctionConditional() {
                                            @Override
                                            public boolean eval(TaskContext context) {
                                                return context.result().size() == 0;
                                            }
                                        }, fromVar("jsonObject")
                                                .action(NEW_NODE_FROM_JSON, "")
                                                .indexNode("stations", "contract_name,name")
                                                .isolate(asVar("parent").traverse("position").then(new Action() {
                                                    @Override
                                                    public void eval(TaskContext context) {
                                                        KDTree tree = (KDTree) context.variable("tree").get(0);
                                                        Node n = context.resultAsNodes().get(0);
                                                        tree.insert(
                                                                new double[]{(double) n.get("lat"), (double) n.get("lng")},
                                                                (Node) context.variable("parent").get(0),
                                                                new Callback<Boolean>() {
                                                                    @Override
                                                                    public void on(Boolean result) {
                                                                        context.continueTask();
                                                                    }
                                                                });
                                                    }
                                                }))
                                                .print("New Station Created: {{result}}"))
                                        .then(updateJsonFields)
                                        .isolate(asVar("parent").traverse("position").then(new Action() {
                                            @Override
                                            public void eval(TaskContext context) {
                                                KDTree tree = (KDTree) context.variable("tree").get(0);
                                                tree.set(KDTree.);
                                                Node n = context.resultAsNodes().get(0);
                                                tree.insert(
                                                        new double[]{(double) n.get("lat"), (double) n.get("lng")},
                                                        (Node) context.variable("parent").get(0),
                                                        new Callback<Boolean>() {
                                                            @Override
                                                            public void on(Boolean result) {
                                                                context.continueTask();
                                                            }
                                                        });
                                            }
                                        }))
                                .clear()
                        ).print("Update done.")
                        .clear();

        update.execute(graph, null);

    }

    private Action updateJsonFields = new Action() {
        @Override
        public void eval(TaskContext context) {
            Node n = context.resultAsNodes().get(0);
            JsonObject obj = (JsonObject) context.variable("jsonObject").get(0);
            context.setTime(obj.getLong("last_update", context.time()));
            Iterator<JsonObject.Member> mIt = obj.iterator();
            while (mIt.hasNext()) {
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
                                try {
                                    n.set(m.getName(), value.asDouble());
                                }catch (Throwable t) {
                                    t.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }
            context.continueTask();
        }
    };


    public static void main(String[] args) {
        GeoJsonTest test = new GeoJsonTest();
        test.baseTest();
    }


}
