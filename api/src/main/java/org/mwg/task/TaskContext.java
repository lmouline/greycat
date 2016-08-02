package org.mwg.task;

import org.mwg.Graph;
import org.mwg.Node;

import java.util.Map;

public interface TaskContext {

    Graph graph();

    long world();

    void setWorld(long world);

    long time();

    void setTime(long time);

    TaskResult variable(String name);

    TaskResult wrap(Object input);

    TaskResult wrapClone(Object input);

    TaskResult newResult();

    void defineVariable(String name);

    void defineVariableWith(String name, Object initialResult);

    void setGlobalVariable(String name, Object value);

    void setVariable(String name, Object value);

    void addToGlobalVariable(String name, Object value);

    void addToVariable(String name, Object value);

    //Object based results
    TaskResult result();

    TaskResult<Node> resultAsNodes();

    TaskResult<String> resultAsStrings();

    void continueTask();

    void continueWith(TaskResult nextResult);

    String template(String input);

    TaskHook hook();

    int ident();

}
