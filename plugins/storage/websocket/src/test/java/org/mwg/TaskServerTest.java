package org.mwg;

import org.mwg.plugin.TaskExecutor;
import org.mwg.task.TaskResult;

import java.util.Arrays;

import static org.mwg.core.task.Actions.newTask;

public class TaskServerTest {

    public static void main(String[] args) {
        Graph graph = GraphBuilder
                .newBuilder()
                .build();
        graph.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean result) {

                //create graph nodes
                Node n0 = graph.newNode(0, Constants.BEGINNING_OF_TIME);
                n0.set("name", Type.STRING, "n0");
                n0.set("value", Type.INT, 8);

                Node n1 = graph.newNode(0, Constants.BEGINNING_OF_TIME);
                n1.set("name", Type.STRING, "n1");
                n1.set("value", Type.INT, 3);

                Node root = graph.newNode(0, Constants.BEGINNING_OF_TIME);
                root.set("name", Type.STRING, "root");
                root.addToRelation("children", n0);
                root.addToRelation("children", n1);

                //create some index
                graph.index(0, Constants.BEGINNING_OF_TIME, "roots", new Callback<NodeIndex>() {
                    @Override
                    public void on(NodeIndex rootsIndex) {
                        rootsIndex.addToIndex(root, "name");

                        graph.index(0, Constants.BEGINNING_OF_TIME, "nodes", new Callback<NodeIndex>() {
                            @Override
                            public void on(NodeIndex nodesIndex) {
                                nodesIndex.addToIndex(n0, "name");
                                nodesIndex.addToIndex(n1, "name");
                                nodesIndex.addToIndex(root, "name");
                                new WSServer(graph, 4000).start();
                                System.out.println("Server started 4000");


                                WSClient client = new WSClient("ws://localhost:4000");
                                Graph emptyGraph = GraphBuilder
                                        .newBuilder()
                                        .withStorage(client)
                                        .build();
                                emptyGraph.connect(result1 -> {

                                    client.executeTasks(new Callback<String[]>() {
                                        @Override
                                        public void on(String[] results) {
                                            System.out.println("Results");
                                            for (String r : results) {
                                                System.out.println("=>" + r);
                                            }
                                            emptyGraph.disconnect(result2 -> {
                                                graph.disconnect(null);
                                                System.out.println("Should exit now...");
                                            });
                                        }
                                    }, newTask().readGlobalIndex("nodes"), newTask().readGlobalIndex("roots"));




                                /*
                                client.execute(new Callback<TaskResult[]>() {
                                    @Override
                                    public void on(TaskResult[] result) {

                                    }
                                }, newTask().readGlobalIndex("nodes"));
*/
                                });


                            }
                        });

                    }
                });

            }
        });
    }

}
