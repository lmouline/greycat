package org.mwg.task;

import org.mwg.Graph;
import org.mwg.Node;

public interface TaskContext {

    Graph graph();

    long world();

    TaskContext setWorld(long world);

    long time();

    TaskContext setTime(long time);

    TaskResult variable(String name);

    boolean isGlobal(String name);

    TaskResult wrap(Object input);

    TaskResult wrapClone(Object input);

    TaskResult newResult();

    TaskContext declareVariable(String name);

    TaskContext defineVariable(String name, Object initialResult);

    TaskContext defineVariableForSubTask(String name, Object initialResult);

    TaskContext setGlobalVariable(String name, Object value);

    TaskContext setVariable(String name, Object value);

    TaskContext addToGlobalVariable(String name, Object value);

    TaskContext addToVariable(String name, Object value);

    //Object based results
    TaskResult result();

    TaskResult<Node> resultAsNodes();

    TaskResult<String> resultAsStrings();

    void continueTask();

    void continueWith(TaskResult nextResult);

    String template(String input);

    String[] templates(String[] inputs);

}
