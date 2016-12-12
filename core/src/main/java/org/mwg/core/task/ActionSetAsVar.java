package org.mwg.core.task;

import org.mwg.Constants;
import org.mwg.task.Action;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;

class ActionSetAsVar implements Action {

    private final String _name;

    ActionSetAsVar(final String p_name) {
        if (p_name == null) {
            throw new RuntimeException("variableName should not be null");
        }
        this._name = p_name;
    }

    @Override
    public void eval(final TaskContext ctx) {
        final TaskResult previousResult = ctx.result();
        if (ctx.isGlobal(_name)) {
            ctx.setGlobalVariable(ctx.template(_name), previousResult);
        } else {
            ctx.setVariable(ctx.template(_name), previousResult);
        }
        ctx.continueTask();
    }

    @Override
    public void serialize(StringBuilder builder) {
        builder.append(ActionNames.SET_AS_VAR);
        builder.append(Constants.TASK_PARAM_OPEN);
        builder.append(_name);
        builder.append(Constants.TASK_PARAM_CLOSE);
    }

    @Override
    public String toString() {
        final StringBuilder res = new StringBuilder();
        serialize(res);
        return res.toString();
    }

}
