package mwg.sample;

import org.mwg.*;
import org.mwg.ml.MLPlugin;
import org.mwg.ml.algorithm.anomalydetector.InterquartileRangeOutlierDetectorNode;
import org.mwg.ml.algorithm.profiling.GaussianNode;

import static org.mwg.task.Actions.*;


public class DebugServer {

    public static void main(String[] args) {
        Graph g = new GraphBuilder()
                .withMemorySize(100000)
                //.withStorage(new LevelDBStorage("mwg_db"))
                .withPlugin(new MLPlugin())
                .build();
        g.connect(isConnected -> {

            String roomType = InterquartileRangeOutlierDetectorNode.NAME;
            String sensorType = GaussianNode.NAME;

            setTime("0")
                    .setWorld("0")
                    .newTypedNode(roomType).setProperty("name", Type.STRING, "room_0").indexNode("rooms", "name").asVar("room_0")
                    .newTypedNode(roomType).setProperty("name", Type.STRING, "room_01").indexNode("rooms", "name").asVar("room_01")
                    .newTypedNode(roomType).setProperty("name", Type.STRING, "room_001").indexNode("rooms", "name").asVar("room_001")
                    .newTypedNode(roomType).setProperty("name", Type.STRING, "room_0001").indexNode("rooms", "name").asVar("room_0001")
                    .fromVar("room_0").add("rooms", "room_01")
                    .fromVar("room_01").add("rooms", "room_001")
                    .fromVar("room_001").add("rooms", "room_0001")
                    .loop("1","1000", newTypedNode(sensorType)
                            .setProperty("name", Type.STRING, "sensor_{{i}}")
                            .setProperty("id", Type.STRING, "{{i}}")
                            .indexNode("sensors", "id")
                            .defineVar("sensor")
                            .ifThenElse(cond("i % 4 == 0"), fromVar("room_0").add("sensors", "sensor"),
                                    ifThenElse(cond("i % 4 == 1"), fromVar("room_01").add("sensors", "sensor"),
                                            ifThenElse(cond("i % 4 == 2"), fromVar("room_001").add("sensors", "sensor"),
                                                    ifThen(cond("i % 4 == 3"), fromVar("room_0001").add("sensors", "sensor"))))))
                    .execute(g, taskResult -> {
                        if (taskResult != null) {
                            taskResult.free();
                        }
                        new WSServer(g, 8050).start();
                        System.out.println("MWG Server listener through :8050");
                    });

        });
    }

}
