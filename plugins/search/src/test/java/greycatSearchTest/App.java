package greycatSearchTest;

import greycat.*;
import greycat.search.SearchEngine;
import greycat.search.WaveSearch;

import static greycat.Tasks.newTask;

public class App {

    public static void main(String[] args) {

        Graph g = GraphBuilder.newBuilder().build();
        g.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean result) {
                //create a grid of 5x5 nodes
                Task initGrid = newTask();
                initGrid.loop("0", "9", newTask().createNode().setAttribute("name", Type.STRING, "room_{{i}}").addToGlobalIndex("rooms", "name"));
                initGrid.execute(g, new Callback<TaskResult>() {
                    @Override
                    public void on(TaskResult result) {
                        result.free();
                        SearchEngine engine = new WaveSearch();
                        //engine.addAction(newTask().glo);
                        //System.out.println("");
                    }
                });


            }
        });

    }

}
