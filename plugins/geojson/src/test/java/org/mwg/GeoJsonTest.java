package org.mwg;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import org.mwg.plugin.geojson.GeoJsonPlugin;
import org.mwg.structure.StructPlugin;
import org.mwg.structure.tree.KDTree;
import org.mwg.task.*;
import org.mwg.utility.VerboseHookFactory;
import org.mwg.utility.VerbosePlugin;

import java.util.Iterator;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static org.mwg.plugin.geojson.GeoJsonActions.*;
import static org.mwg.task.Actions.*;

public class GeoJsonTest {

    //API KEY:          9c84c41dfc20a1539e3cb1239efa65889b6341e4
    // GetStations:     https://api.jcdecaux.com/vls/v1/stations
    // Contract information GET https://api.jcdecaux.com/vls/v1/stations/{station_number}?contract={contract_name} HTTP/1.1

    private final static String _baseParam = "?apiKey=9c84c41dfc20a1539e3cb1239efa65889b6341e4";
    private final static String _stationsListAddress = "https://api.jcdecaux.com/vls/v1/stations";
    private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();


    //@Test
    public void baseTest() {


        final Graph g = new GraphBuilder().withPlugin(new GeoJsonPlugin()).withPlugin(new StructPlugin()).withPlugin(new VerbosePlugin()).build();
        g.connect(connectionResult -> {

            WSServer graphServer = new WSServer(g, 8050);
            graphServer.start();

            while (true) {
                update(g);
                try {
                    Thread.sleep(30 * 1000);
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


    final Task getOrCreatePositionsTree =
            fromIndexAll("positionsTree")
                    .ifThen(new TaskFunctionConditional() {
                        @Override
                        public boolean eval(TaskContext context) {
                            return context.result().size() == 0;
                        }
                    }, setTime("" + Constants.BEGINNING_OF_TIME).newTypedNode(KDTree.NAME).print("Positions Tree created !").indexNode("positionsTree", ""))
                    .asVar("tree");

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
                                } catch (Throwable t) {
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

    final Task updateFromJsonValue =
            setTime(System.currentTimeMillis() + "")
                    .asVar("jsonObject")
                    .then(new Action() {
                        @Override
                        public void eval(TaskContext context) {
                            JsonObject obj = (JsonObject) context.result().get(0);

                            Query q = context.graph().newQuery();
                            q.setTime(context.time());
                            q.setWorld(context.world());
                            q.setIndexName("stations");
                            q.add("contract_name", obj.get("contract_name").asString());
                            q.add("name", obj.get("name").asString());

                            context.graph().findByQuery(q, new Callback<Node[]>() {
                                @Override
                                public void on(Node[] result) {
                                    TaskResult<Node> newRes = context.newResult();
                                    if (result.length > 0) {
                                        newRes.add(result[0]);
                                    }
                                    context.continueWith(newRes);
                                    context.graph().freeNodes(result);
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
                                    tree.insertWith(
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
                            // tree.set(KDTree.);
                            Node n = context.resultAsNodes().get(0);
                            tree.insertWith(
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
                    .clear();

    final Task update =
            hook(new VerboseHookFactory())
                    .subTask(getOrCreatePositionsTree)
                    .action(LOADJSON, _stationsListAddress + _baseParam)
                    .foreach(updateFromJsonValue)
                    .print("Update done.")
                    .clear();

    private void update(Graph graph) {

        System.out.println("Updating");
        update.execute(graph, null);

    }


    public static void main(String[] args) {
        GeoJsonTest test = new GeoJsonTest();
        test.baseTest();
    }


}
