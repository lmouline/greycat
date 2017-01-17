package org.mwg.internal.task;

import org.mwg.task.Action;
import org.mwg.task.Task;
import org.mwg.task.TaskContext;

import java.util.Map;

public abstract class CF_Action implements Action {

    abstract public Task[] children();

    abstract public void cf_serialize(StringBuilder builder, Map<Integer, Integer> dagIDS);

    @Override
    public abstract void eval(TaskContext ctx);

    @Override
    public void serialize(StringBuilder builder) {
        throw new RuntimeException("serialization error !!!");
    }

    @Override
    public String toString() {
        final StringBuilder res = new StringBuilder();
        cf_serialize(res, null);
        return res.toString();
    }

}
