package org.mwg.debug;

import org.mwg.task.TaskAction;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskHook;
import org.mwg.utility.HashHelper;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class JsonHook implements TaskHook {

    private Map<TaskContext, Integer> ctxIdents = new HashMap<TaskContext, Integer>();
    private StringBuilder jsonTask = new StringBuilder();
    private StringBuilder jsonContext = new StringBuilder();

    private HashHelper hashHelper = new HashHelper();

    private int taskID;

    // called at the beginning of the task
    @Override
    public synchronized void start(TaskContext initialContext) {
        taskID = initialContext.hashCode();

        ctxIdents.put(initialContext, 1);
        //System.out.println("StartTask:" + initialContext);

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
        Integer currentPrefix = ctxIdents.get(context);
        jsonTask.append("\n");
        createTabs(jsonTask, currentPrefix);
        jsonTask.append("{\n");
        createTabs(jsonTask, currentPrefix + 1);
        String taskName = action.toString();
        jsonTask.append("\"id\" : ");
        jsonTask.append(hashHelper.hashBytes((action.toString() + action.hashCode() + context.hashCode()).getBytes()));
        jsonTask.append(",\n");

        createTabs(jsonTask, currentPrefix + 1);
        jsonTask.append("\"action\" : \"");
        jsonTask.append(context.template(taskName));
        jsonTask.append("\"");
        jsonTask.append(",\n");

        createTabs(jsonTask, currentPrefix + 1);
        jsonTask.append("\"children\" : [");


        jsonContext.append("\n\t{\n\t\t");
        jsonContext.append("\"id\" : ");
        jsonContext.append(hashHelper.hashBytes((action.toString() + action.hashCode() + context.hashCode()).getBytes()));
        jsonContext.append(",\n\t\t");
        jsonContext.append("\"context\" : ");
        jsonContext.append(context.result().toString());
        jsonContext.append("\n\t},");
    }

    // called after every action of the task
    @Override
    public synchronized void afterAction(TaskAction action, TaskContext context) {
        Integer currentPrefix = ctxIdents.get(context);

        //removing the spare commas
        removeLastChar(jsonTask, ',');

        jsonTask.append("]\n");
        createTabs(jsonTask, currentPrefix);
        jsonTask.append("},");
    }

    // called before a subtask (the tasks inside a repeat, inside a ifThen...)
    @Override
    public synchronized void beforeTask(TaskContext parentContext, TaskContext context) {
        Integer currentPrefix = ctxIdents.get(parentContext);
        ctxIdents.put(context, currentPrefix + 1);
    }

    // called after a subtask (the tasks inside a repeat, inside a ifThen...)
    @Override
    public synchronized void afterTask(TaskContext context) {
        ctxIdents.remove(context);
    }

    // called at the end of the task
    @Override
    public synchronized void end(TaskContext finalContext) {
        //removing the last comma
        removeLastChar(jsonTask, ',');
        jsonTask.append("]\n}");

        removeLastChar(jsonContext, ',');
        jsonContext.append("]\n}");
        //System.out.println("EndTask:" + finalContext.toString());

        File jsonDir = new File("JSON");
        jsonDir.mkdir();

        try (Writer taskWriter = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("JSON/task_" + taskID + ".json"), "utf-8"))) {
            taskWriter.write(String.valueOf(jsonTask));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (Writer contextWriter = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("JSON/context_" + taskID + ".json"), "utf-8"))) {
            contextWriter.write(String.valueOf(jsonContext));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create n tabs in the StringBuilder sb
     * @param n
     */
    private synchronized void createTabs(StringBuilder sb, int n){
        String str = "";
        for (int i = 0; i < n; i++) {
            str += "\t";
        }
        sb.append(str);
    }

    /**
     * Remove the last char of the StringBuilder sb if this char is the parameter c
     * @param sb
     * @param c
     */
    private synchronized void removeLastChar(StringBuilder sb, char c){
        int length = sb.length();
        if (sb.charAt(length - 1) == c){
            sb.setLength(length - 1);
        }
    }
}
