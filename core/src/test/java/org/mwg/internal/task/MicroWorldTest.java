package org.mwg.internal.task;

import org.mwg.Callback;
import org.mwg.Graph;
import org.mwg.GraphBuilder;
import org.mwg.Type;

import static org.mwg.internal.task.CoreActions.*;
import static org.mwg.task.Tasks.newTask;

public class MicroWorldTest {

    public static void main(String[] args) {

        Graph g = new GraphBuilder()
                .withMemorySize(10000)
                .withPlugin(new org.mwg.utility.VerbosePlugin())
                .build();
        g.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean isConnected) {

                newTask().loopPar("1", "2",
                        newTask()
                                .then(createNode())
                                .then(setAttribute("name", Type.STRING, "room_{{i}}"))
                                .then(addToGlobalIndex("rooms", "name"))
                                .then(defineAsVar("parentRoom"))
                                .loop("1", "3",
                                        newTask()
                                                .then(createNode())
                                                .then(setAttribute("sensor", Type.STRING, "sensor_{{i}}"))
                                        //.then(addTo("sensors", "parentRoom"))
                                )
                ).execute(g, null);


                /*
                loop("0", "3",
                        newNode()
                        .setAttribute("name", Type.STRING, "node_{{i}}")
                        .print("{{result}}")
                )
                .execute(g,null);
*/

            }
        });


    }

}
