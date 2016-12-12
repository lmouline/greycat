package org.mwg.core.task;

import org.mwg.Constants;
import org.mwg.task.Action;
import org.mwg.task.TaskContext;

class ActionClearResult implements Action {

    @Override
    public void eval(final TaskContext ctx) {
        ctx.continueWith(ctx.newResult());
    }

    @Override
    public void serialize(StringBuilder builder) {
        builder.append(ActionNames.CLEAR_RESULT);
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
