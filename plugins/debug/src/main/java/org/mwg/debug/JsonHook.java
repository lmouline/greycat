package org.mwg.debug;

import org.mwg.task.TaskAction;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskHook;
import org.mwg.utility.HashHelper;

import java.io.*;

class JsonHook implements TaskHook {

    private TaskTraceRegistry registry;

    public JsonHook(TaskTraceRegistry p_registry) {
        this.registry = p_registry;
    }

    private StringBuilder jsonTask = new StringBuilder();
    private StringBuilder jsonContext = new StringBuilder();

    private HashHelper hashHelper = new HashHelper();

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

        jsonContext.append("{\n\t");
        jsonContext.append("\"id\" : ");
        jsonContext.append(initialContext.hashCode());
        jsonContext.append(",\n\t");
        jsonContext.append("\"type\" : \"context\"");
        jsonContext.append(",\n\t");
        jsonContext.append("\"actions\" : [");
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
        jsonContext.append("\n\t{\n\t\t");
        jsonContext.append("\"id\" : ");
        jsonContext.append((int) HashHelper.hashBytes((action.toString() + action.hashCode() + context.hashCode()).getBytes()));
        jsonContext.append(",\n\t\t");
        jsonContext.append("\"context\" : ");
        jsonContext.append(context.result().toString());
        jsonContext.append("\n\t},");
    }

    // called after every action of the task
    @Override
    public synchronized void afterAction(TaskAction action, TaskContext context) {
        //removing the spare commas
        removeLastChar(jsonTask, ',');
        jsonTask.append("]\n");
        jsonTask.append("},");
    }

    // called before a subtask (the tasks inside a repeat, inside a ifThen...)
    @Override
    public void beforeTask(TaskContext parentContext, TaskContext context) {

    }

    // called after a subtask (the tasks inside a repeat, inside a ifThen...)
    @Override
    public void afterTask(TaskContext context) {

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

    /**
     * Remove the last char of the StringBuilder sb if this char is the parameter c
     *
     * @param sb
     * @param c
     */
    private void removeLastChar(StringBuilder sb, char c) {
        int length = sb.length();
        if (sb.charAt(length - 1) == c) {
            sb.setLength(length - 1);
        }
    }
}
