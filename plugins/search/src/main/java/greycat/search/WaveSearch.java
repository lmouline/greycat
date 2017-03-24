package greycat.search;

import greycat.*;
import greycat.struct.Tree;

import static greycat.Tasks.newTask;

public class WaveSearch extends BaseEngine {

    @Override
    public void explore(Callback<Node> callback) {
        long indexWorld = _graph.fork(_iWorld);
        Node indexNode = _graph.newNode(indexWorld, Constants.BEGINNING_OF_TIME);
        Tree indexSolutions = (Tree) indexNode.getOrCreate("solutions", Type.KDTREE);
        Task t = newTask();
        t.loop("0", "9", newTask()).execute(_graph, new Callback<TaskResult>() {
            @Override
            public void on(TaskResult result) {
                result.free();
                callback.on(indexNode);
            }
        });
    }

}
