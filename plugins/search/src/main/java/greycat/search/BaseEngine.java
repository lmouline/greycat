package greycat.search;

import greycat.Graph;
import greycat.Task;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseEngine implements SearchEngine {

    protected final List<Task> _tasks = new ArrayList<Task>();
    protected final List<Task> _functions = new ArrayList<Task>();
    protected Graph _graph;
    protected long _iWorld;

    public void addAction(Task task) {
        _tasks.add(task);
    }

    public void addFunction(Task function) {
        _functions.add(function);
    }

    public void init(Graph g, long iWorld){
        _graph = g;
        _iWorld = iWorld;
    }

}
