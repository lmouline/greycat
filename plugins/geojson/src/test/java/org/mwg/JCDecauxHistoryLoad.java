package org.mwg;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import org.mwg.importer.ImporterPlugin;
import org.mwg.ml.MLPlugin;
import org.mwg.ml.algorithm.profiling.GaussianSlotNode;
import org.mwg.ml.algorithm.regression.PolynomialNode;
import org.mwg.plugin.geojson.GeoJsonPlugin;
import org.mwg.structure.StructurePlugin;
import org.mwg.structure.action.NTreeInsertTo;
import org.mwg.structure.distance.Distances;
import org.mwg.structure.tree.KDTree;
import org.mwg.task.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;

import static org.mwg.importer.ImporterActions.READFILES;
import static org.mwg.importer.ImporterActions.readFiles;
import static org.mwg.plugin.geojson.GeoJsonActions.loadJson;
import static org.mwg.task.Actions.*;

public class JCDecauxHistoryLoad {

    //public static final String dataSetFolder = "/Users/gnain/Sources/Kevoree-Modeling/mwDB/plugins/geojson/dataset_jcd/dataset";
    public static final String dataSetFolder = "/Users/gnain/Sources/Kevoree-Modeling/mwDB/plugins/geojson/dataset_jcd/subset";


    //@Test
    public void baseTest() {


        final Graph g = new GraphBuilder().withStorage(new LevelDBStorage("tempStorage")).withMemorySize(200000).withPlugin(new ImporterPlugin()).withPlugin(new GeoJsonPlugin()).withPlugin(new StructurePlugin()).withPlugin(new MLPlugin()).build();
        g.connect(connectionResult -> {

            WSServer graphServer = new WSServer(g, 8050);
            graphServer.start();

            Task processFiles = newTask()
                    .inject(System.currentTimeMillis())
                    .asVar("processStartTime")
                    .action(READFILES, dataSetFolder)
                    .foreach(
                            print("Processing file {{i}}:{{result}} ")
                                    .then(context->{
                                        String filePath = (String) context.result().get(0);
                                        String finaleName = filePath.substring(filePath.lastIndexOf("/")+1, filePath.length()-5);
                                        LocalDateTime ldt = LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(finaleName)*1000), ZoneId.systemDefault());
                                        System.out.println(ldt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy H:mm:ss")));
                                        context.continueTask();
                                    })
                                    .subTask(processFile)
                                    .save()
                                    .clear())
                    .then(context -> {
                        long start = (long)context.variable("processStartTime").get(0);
                        long duration = System.currentTimeMillis() - start;
                        System.out.println("Duration: " + duration/(60*1000) + "m" + (int)((duration%(60*1000))/1000) + "s");
                        context.continueTask();
                    });

            TaskContext context = processFiles.prepareWith(g, null, new Callback<TaskResult>() {
                @Override
                public void on(TaskResult result) {
                    System.out.println(result.size());
                }
            });
            context.setVariable("processTime", System.currentTimeMillis());
            processFiles.executeUsing(context);

        });
    }

    final Task processFile =
            loadJson("{{result}}")
                    .setTime("{{processTime}}")
                    .asVar("jsonFileContent")
                    .foreach(
                            isolate(
                                    asVar("jsonObject")
                                            .map(jsonValue -> ((JsonObject) jsonValue).get("contract_name").asString())
                                            .asVar("cityName")
                                            //.print("City Name: {{cityName}}")
                                            .fromVar("jsonObject")
                                            .map(jsonValue -> ((JsonObject) jsonValue).get("name").asString())
                                            .asVar("stationName")
                                            .fromVar("jsonObject")
                                            .map(jsonValue -> ((JsonObject) jsonValue).get("last_update").asLong())
                                            .asVar("lastUpdateTime")
                                            //.print("Processing Station: {{stationName}}")
                                            .fromIndex("cities", "name={{cityName}}")
                                            .ifThen((context -> context.result().size() == 0),
                                                    newNode()
                                                            .setProperty("name", Type.STRING, "{{cityName}}")
                                                            .asVar("cityNode")
                                                            .indexNodeAt("0", "0", "cities", "name")
                                                            .setTime("" + Constants.BEGINNING_OF_TIME)
                                                            .newTypedNode(KDTree.NAME)
                                                            .setProperty(KDTree.DISTANCE, Type.INT, Distances.GEODISTANCE + "")
                                                            .setProperty(KDTree.FROM, Type.STRING, "position.lat,position.lng")
                                                            .asVar(POSITIONS_TREE)
                                                            .fromVar("cityNode")
                                                            //.setTime("{{processTime}}")
                                                            .add("positions", POSITIONS_TREE)
                                                            .print("City Created: {{cityName}}\n")

                                            ).asVar("cityNode")
                                            //.print("City Node: {{result}}")
                                            .traverse("positions")
                                            .asVar(POSITIONS_TREE)
                                            //.print("Positions Tree: {{result}}")
                                            .fromVar("cityNode")
                                            .traverseIndex("stations", "name", "{{stationName}}")
                                            .ifThen((context -> context.result().size() == 0),
                                                    then(context -> createStationNode(context))
                                                            .print("Station Created: {{stationName}}\n")
                                                            .asVar(STATION_NODE)
                                                            .fromVar("cityNode")
                                                            .localIndex("stations", "name", STATION_NODE)
                                                            .fromVar(STATION_NODE)
                                                    //.print("Station Indexed: {{stationName}}")
                                            ).asVar(STATION_NODE)
                                            .jump("{{lastUpdateTime}}")
                                            .asVar(STATION_NODE)
                                            .traverse(POSITION)
                                            .asVar(POSITION_NODE)
                                            .fromVar(STATION_NODE)
                                            .traverse(AVAILABLE_STANDS_PROFILE)
                                            .asVar(AVAILABLE_STANDS_PROFILE_NODE)
                                            .fromVar(STATION_NODE)
                                            .traverse(AVAILABLE_BIKES_PROFILE)
                                            .asVar(AVAILABLE_BIKES_PROFILE_NODE)
                                            .fromVar(STATION_NODE)
                                            .then(context -> updateStationValues(context))
                                            .action(NTreeInsertTo.NAME, POSITIONS_TREE)
                                            .traverse(AVAILABLE_BIKES)
                                            .setProperty("value", Type.DOUBLE, "{{" + AVAILABLE_BIKES_VALUE + "}}")
                                            .fromVar(STATION_NODE)
                                            .traverse(AVAILABLE_STANDS)
                                            .setProperty("value", Type.DOUBLE, "{{" + AVAILABLE_STANDS_VALUE + "}}")
                                            .clear()
                            ).clear()
                    ).print("File Completed.\n")
                    .clear();


    public void createStationNode(TaskContext context) {

        Graph g = context.graph();

        JsonObject obj = (JsonObject) context.variable("jsonObject").get(0);
        context.setTime(obj.getLong("last_update", context.time()));

        Node n = g.newNode(context.world(), context.time());
        n.set("name", obj.get("name").asString());

        n.add(POSITION, g.newNode(context.world(), context.time()));

        Node polyBikes = g.newTypedNode(context.world(), context.time(), PolynomialNode.NAME);
        polyBikes.set(PolynomialNode.PRECISION, 1.);
        n.add(AVAILABLE_BIKES, polyBikes);

        Node profileBikes = g.newTypedNode(context.world(), context.time(), GaussianSlotNode.NAME);
        profileBikes.setProperty(GaussianSlotNode.SLOTS_NUMBER, Type.INT, 7 * 24);
        profileBikes.setProperty(GaussianSlotNode.PERIOD_SIZE, Type.LONG, 7 * 24 * 3600 * 1000);
        n.add(AVAILABLE_BIKES_PROFILE, profileBikes);

        Node polyStands = g.newTypedNode(context.world(), context.time(), PolynomialNode.NAME);
        polyStands.set(PolynomialNode.PRECISION, 1.);
        n.add(AVAILABLE_STANDS, polyStands);

        Node profileStands = g.newTypedNode(context.world(), context.time(), GaussianSlotNode.NAME);
        profileStands.setProperty(GaussianSlotNode.SLOTS_NUMBER, Type.INT, 7 * 24);
        profileStands.setProperty(GaussianSlotNode.PERIOD_SIZE, Type.LONG, 7 * 24 * 3600 * 1000);
        n.add(AVAILABLE_STANDS_PROFILE, profileStands);

        TaskResult<Node> res = context.newResult();
        res.add(n);
        context.continueWith(res);
    }

    private void updateStationValues(TaskContext context) {

        Node stationNode = (Node) context.variable("stationNode").get(0);
        Node positionNode = (Node) context.variable(POSITION_NODE).get(0);

        GaussianSlotNode availableStandsProfileNode = (GaussianSlotNode) context.variable(AVAILABLE_STANDS_PROFILE_NODE).get(0);
        GaussianSlotNode availableBikesProfileNode = (GaussianSlotNode) context.variable(AVAILABLE_BIKES_PROFILE_NODE).get(0);

        JsonObject obj = (JsonObject) context.variable("jsonObject").get(0);

        Iterator<JsonObject.Member> mIt = obj.iterator();
        while (mIt.hasNext()) {
            JsonObject.Member m = mIt.next();
            JsonValue value = m.getValue();
            String name = m.getName();
            if (name.equals(POSITION)) {
                JsonObject posJson = obj.get(POSITION).asObject();
                Iterator<JsonObject.Member> mIt2 = posJson.iterator();
                while (mIt2.hasNext()) {
                    JsonObject.Member p = mIt2.next();
                    positionNode.set(p.getName(), p.getValue().asDouble());
                }
            } else if (name.equals(AVAILABLE_BIKES)) {
                //System.out.println("Available Bikes learn " + value.asDouble() + " at " + availableBikesProfileNode.time());
                availableBikesProfileNode.learnArray(new double[]{value.asDouble()});
                context.setVariable(AVAILABLE_BIKES_VALUE, value.asDouble());

            } else if (name.equals(AVAILABLE_STANDS)) {
                //System.out.println("Available Stands learn " + value.asDouble() + " at " + availableBikesProfileNode.time());
                availableStandsProfileNode.learnArray(new double[]{value.asDouble()});
                context.setVariable(AVAILABLE_STANDS_VALUE, value.asDouble());
            } else {
                if (value.isString()) {
                    stationNode.set(m.getName(), value.asString());
                } else if (value.isBoolean()) {
                    stationNode.set(m.getName(), value.asBoolean());
                } else if (value.isNumber()) {
                    try {
                        stationNode.set(m.getName(), value.asInt());
                    } catch (Exception e) {
                        try {
                            stationNode.set(m.getName(), value.asLong());
                        } catch (Exception e2) {
                            try {
                                stationNode.set(m.getName(), value.asFloat());
                            } catch (Exception e3) {
                                try {
                                    stationNode.set(m.getName(), value.asDouble());
                                } catch (Throwable t) {
                                    t.printStackTrace();
                                    context.continueTask();
                                }
                            }
                        }
                    }
                }
            }
        }
        context.continueTask();

    }

    public static final String POSITION = "position";
    public static final String AVAILABLE_BIKES = "available_bikes";
    public static final String AVAILABLE_STANDS = "available_bike_stands";
    public static final String AVAILABLE_BIKES_PROFILE = "available_bike_stands_profile";
    public static final String AVAILABLE_STANDS_PROFILE = "available_bikes_profile";

    public static final String STATION_NODE = "stationNode";
    public static final String POSITIONS_TREE = "positionsTree";
    public static final String POSITION_NODE = "positionNode";
    public static final String AVAILABLE_BIKES_PROFILE_NODE = "available_bike_stands_profileNode";
    public static final String AVAILABLE_STANDS_PROFILE_NODE = "available_bikes_profileNode";
    public static final String AVAILABLE_BIKES_VALUE = "available_bike_stands_val";
    public static final String AVAILABLE_STANDS_VALUE = "available_bikes_val";

    public static void main(String[] args) {
        JCDecauxHistoryLoad test = new JCDecauxHistoryLoad();
        test.baseTest();
    }

}
