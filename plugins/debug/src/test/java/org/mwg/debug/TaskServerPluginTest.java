package org.mwg.debug;

import org.mwg.Graph;
import org.mwg.GraphBuilder;
import org.mwg.Type;

import static org.mwg.task.Actions.newNode;

public class TaskServerPluginTest {

    public static void main(String[] args) {
        Graph g = new GraphBuilder().withPlugin(new TaskServerPlugin(8077)).build();
        g.connect(result -> {

            newNode().setProperty("name", Type.STRING, "hello").execute(g, null);

            //g.disconnect(null);
        });

    }

}
