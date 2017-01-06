package org.mwg;

import org.mwg.task.Task;
import org.mwg.task.TaskResult;
import org.mwg.task.TaskResultIterator;

public class TaskError implements TaskResult {

    private Task task;
    private String message;

    public TaskError(Task task,String message) {
        this.task = task;
        this.message = message;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        builder.append("\"error\":\"" + message + "\",");
        builder.append("\"parsed\":\"" + task.toString() + "\"");
        builder.append("}");
        return builder.toString();
    }

    @Override
    public TaskResultIterator iterator() {
        return null;
    }

    @Override
    public Object get(int index) {
        return null;
    }

    @Override
    public TaskResult set(int index, Object input) {
        return null;
    }

    @Override
    public TaskResult allocate(int index) {
        return null;
    }

    @Override
    public TaskResult add(Object input) {
        return null;
    }

    @Override
    public TaskResult clear() {
        return null;
    }

    @Override
    public TaskResult clone() {
        return null;
    }

    @Override
    public void free() {

    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public Object[] asArray() {
        return new Object[0];
    }

    @Override
    public Exception exception() {
        return null;
    }
}
