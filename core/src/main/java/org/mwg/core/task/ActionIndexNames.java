package org.mwg.core.task;

import org.mwg.Callback;
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
    public String toString() {
        return "indexNames()";
    }
}
