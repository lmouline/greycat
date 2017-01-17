package org.mwg.internal.task;

import org.mwg.Constants;
import org.mwg.task.Action;
import org.mwg.task.TaskContext;

class ActionPrint implements Action {

    private final String _name;
    private final boolean _withLineBreak;

    ActionPrint(final String p_name, boolean withLineBreak) {
        this._name = p_name;
        this._withLineBreak = withLineBreak;
    }

    @Override
    public void eval(final TaskContext ctx) {
        if (_withLineBreak) {
            ctx.append(ctx.template(_name) + '\n');
        } else {
            ctx.append(ctx.template(_name));
        }
        ctx.continueTask();
    }

    @Override
    public void serialize(StringBuilder builder) {
        if (_withLineBreak) {
            builder.append(CoreActionNames.PRINTLN);
        } else {
            builder.append(CoreActionNames.PRINT);
        }
        builder.append(Constants.TASK_PARAM_OPEN);
        TaskHelper.serializeString(_name, builder,true);
        builder.append(Constants.TASK_PARAM_CLOSE);
    }

    @Override
    public String toString() {
        final StringBuilder res = new StringBuilder();
        serialize(res);
        return res.toString();
    }

}
