package org.mwg.internal.task;

import org.mwg.Constants;
import org.mwg.task.Action;
import org.mwg.task.TaskContext;

class ActionLog implements Action {

    private final String _value;

    ActionLog(final String p_value) {
        this._value = p_value;
    }

    @Override
    public void eval(final TaskContext ctx) {
        System.out.println(ctx.template(_value));
        ctx.continueTask();
    }

    @Override
    public void serialize(StringBuilder builder) {
        builder.append(CoreActionNames.LOG);
        builder.append(Constants.TASK_PARAM_OPEN);
        TaskHelper.serializeString(_value, builder, true);
        builder.append(Constants.TASK_PARAM_CLOSE);
    }

    @Override
    public String toString() {
        final StringBuilder res = new StringBuilder();
        serialize(res);
        return res.toString();
    }

}
