package org.mwg;

import org.mwg.core.scheduler.NoopScheduler;

public class TaskServerTest {

    public static void main(String[] args) {
        Graph graph = GraphBuilder
                .newBuilder()
                .build();
        graph.connect(result -> {

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
                            new WSServer(graph,4000).start();
                            System.out.println("Server started 4000");
                        }
                    });

                }
            });

        });
    }

}
