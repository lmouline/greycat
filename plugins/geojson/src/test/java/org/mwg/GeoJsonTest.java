package org.mwg;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import org.mwg.plugin.geojson.GeoJsonPlugin;
import org.mwg.plugin.geojson.JsonResult;
import org.mwg.structure.StructurePlugin;
import org.mwg.structure.action.NTreeInsertTo;
import org.mwg.structure.distance.Distances;
import org.mwg.structure.tree.KDTree;
import org.mwg.task.*;

import java.util.Iterator;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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


        final Graph g = new GraphBuilder().withMemorySize(10000000).withPlugin(new GeoJsonPlugin()).withPlugin(new StructurePlugin()).build();
        g.connect(connectionResult -> {

            WSServer graphServer = new WSServer(g, 8050);
            graphServer.start();

            normalRun(g);

        });
    }

    private void runForDebug(Graph graph) {
        loadJson(_stationsListAddress + _baseParam).execute(graph, new Callback<TaskResult>() {
            @Override
            public void on(TaskResult resultA) {

                executor.scheduleAtFixedRate(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            TaskResultIterator<JsonValue> it = ((JsonResult) resultA).iterator();
                            JsonValue next = null;
                            while ((next = it.next()) != null) {
                                ((JsonObject) next).set("last_update", System.currentTimeMillis());
                                ((JsonObject) next).set("available_bike_stands", ((JsonObject) next).getInt("available_bike_stands", 0) + 1);
                            }

                            TaskContext context = update.prepareWith(graph, resultA.asArray(), new Callback<TaskResult>() {
                                @Override
                                public void on(TaskResult result) {
                                    result.free();
                                }
                            });
                            context.setGlobalVariable("processTime", System.currentTimeMillis());
                            update.executeUsing(context);
                        } catch (Throwable t) {
                            t.printStackTrace();
                        }
                    }
                }, 0, 5, TimeUnit.SECONDS);
            }
        });
    }

    private void normalRun(Graph graph) {
        executor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    update(graph);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }, 0, 30, TimeUnit.SECONDS);
    }


    final Task getOrCreatePositionsTree =
            isolate(fromIndexAll("positionsTree")
                    .ifThen(new TaskFunctionConditional() {
                        @Override
                        public boolean eval(TaskContext context) {
                            return context.result().size() == 0;
                        }
                    }, setTime("" + Constants.BEGINNING_OF_TIME)
                            .newTypedNode(KDTree.NAME)
                            .setProperty(KDTree.DISTANCE, Type.INT, Distances.GEODISTANCE + "")
                            .setProperty(KDTree.FROM, Type.STRING, "position.lat,position.lng")
                            .print("Positions Tree created !").indexNode("positionsTree", ""))
                    .asGlobalVar("tree"));

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
            setTime("{{processTime}}")
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
                            .print("New Station Created: {{result}}"))
                    .then(updateJsonFields)
                    .action(NTreeInsertTo.NAME, "tree")
                    .clear();

    final Task update =
            //newTask()
            loadJson(_stationsListAddress + _baseParam)
                    //.hook(new VerboseHookFactory())
                    .subTask(getOrCreatePositionsTree)
                    //.action(LOADJSON, _stationsListAddress + _baseParam)
                    .foreachPar(updateFromJsonValue)
                    .print("Update done.")
                    .clear();

    private void update(Graph graph) {

        System.out.println("Updating");
        TaskContext context = update.prepareWith(graph, null, new Callback<TaskResult>() {
            @Override
            public void on(TaskResult result) {
                result.free();
            }
        });
        context.setGlobalVariable("processTime", System.currentTimeMillis());
        update.executeUsing(context);


    }


    public static void main(String[] args) {
        GeoJsonTest test = new GeoJsonTest();
        test.baseTest();
    }


}
