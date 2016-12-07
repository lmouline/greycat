package org.mwg.core.task;

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
    public String toString() {
        return "setAsVar(\'" + _name + "\')";
    }

}
