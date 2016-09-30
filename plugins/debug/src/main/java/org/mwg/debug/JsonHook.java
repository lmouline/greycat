package org.mwg.debug;

import org.mwg.task.TaskAction;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskHook;
import org.mwg.utility.HashHelper;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class JsonHook implements TaskHook {

    private TaskTraceRegistry registry;

    JsonHook(TaskTraceRegistry p_registry) {
        this.registry = p_registry;
    }

    private Map<TaskContext, List> buffers = new HashMap<TaskContext, List>();

    private StringBuilder jsonTask = new StringBuilder();

    private int taskID;

    // called at the beginning of the task
    @Override
    public synchronized void start(TaskContext initialContext) {
        taskID = initialContext.hashCode();
        jsonTask.append("{\n\t");
        jsonTask.append("\"id\" : ");
        jsonTask.append(initialContext.hashCode());
        jsonTask.append(",\n\t");
        jsonTask.append("\"action\" : \"task\"");
        jsonTask.append(",\n\t");
        jsonTask.append("\"children\" : [");

    }

    // called at the end of the task
    @Override
    public synchronized void end(TaskContext finalContext) {
        //removing the last comma
        removeLastChar(jsonTask, ',');
        jsonTask.append("]\n}");
        removeLastChar(jsonContext, ',');
        jsonContext.append("]\n}");

        registry.tasks.put(taskID, jsonTask.toString());
        registry.contexts.put(taskID, jsonContext.toString());

    }

    // called before every action of the task
    @Override
    public synchronized void beforeAction(TaskAction action, TaskContext context) {
        jsonTask.append("\n");
        jsonTask.append("{\n");
        String taskName = action.toString();
        jsonTask.append("\"id\" : ");
        jsonTask.append((int) HashHelper.hashBytes((action.toString() + action.hashCode() + context.hashCode()).getBytes()));
        jsonTask.append(",\n");
        jsonTask.append("\"action\" : \"");
        jsonTask.append(context.template(taskName));
        jsonTask.append("\"");
        jsonTask.append(",\n");
        jsonTask.append("\"children\" : [");

    }

    // called after every action of the task
    @Override
    public synchronized void afterAction(TaskAction action, TaskContext context) {
        String actionCtxKey = HashHelper.hashBytes((action.toString() + action.hashCode() + context.hashCode()).getBytes()) + "";
        registry.contexts.put(actionCtxKey, context.toString());
    }

    // called before a subtask (the tasks inside a repeat, inside a ifThen...)
    @Override
    public void beforeTask(TaskContext parentContext, TaskContext context) {

    }

    // called after a subtask (the tasks inside a repeat, inside a ifThen...)
    @Override
    public void afterTask(TaskContext context) {

    }


}
