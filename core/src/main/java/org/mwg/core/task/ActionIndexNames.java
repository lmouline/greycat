package org.mwg.core.task;

import org.mwg.Callback;
import org.mwg.Constants;
import org.mwg.task.Action;
import org.mwg.task.TaskContext;

class ActionIndexNames implements Action {

    @Override
    public void eval(TaskContext ctx) {
        ctx.graph().indexNames(ctx.world(), ctx.time(), new Callback<String[]>() {
            @Override
            public void on(String[] result) {
                ctx.continueWith(ctx.wrap(result));
            }
        });
    }

    @Override
    public void serialize(StringBuilder builder) {
        builder.append(ActionNames.INDEX_NAMES);
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
