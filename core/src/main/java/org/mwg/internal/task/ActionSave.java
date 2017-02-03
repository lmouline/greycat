package org.mwg.internal.task;

import org.mwg.Callback;
import org.mwg.Constants;
import org.mwg.task.Action;
import org.mwg.task.TaskContext;

class ActionSave implements Action {

    @Override
    public void eval(final TaskContext ctx) {
        ctx.graph().save(new Callback<Boolean>() {
            @Override
            public void on(Boolean result) {
                ctx.continueTask();
            }
        });
    }

    @Override
    public void serialize(StringBuilder builder) {
        builder.append(CoreActionNames.SAVE);
        builder.append(Constants.TASK_PARAM_OPEN);
        builder.append(Constants.TASK_PARAM_CLOSE);
    }

    @Override
    public String toString() {
        final StringBuilder res = new StringBuilder();
        serialize(res);
        return res.toString();
    }

}
