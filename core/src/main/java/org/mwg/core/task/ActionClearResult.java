package org.mwg.core.task;

import org.mwg.task.Action;
import org.mwg.task.TaskContext;

class ActionClearResult implements Action {

    @Override
    public void eval(final TaskContext ctx) {
        ctx.continueWith(ctx.newResult());
    }

    @Override
    public String toString() {
        return "clearResult()";
    }

}
