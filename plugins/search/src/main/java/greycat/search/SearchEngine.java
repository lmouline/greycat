package greycat.search;

import greycat.Callback;
import greycat.Graph;
import greycat.Node;
import greycat.Task;

public interface SearchEngine {

    void init(Graph g, long world);

    void addAction(Task task);

    void addFunction(Task function);

    void explore(Callback<Node> callback);

}
