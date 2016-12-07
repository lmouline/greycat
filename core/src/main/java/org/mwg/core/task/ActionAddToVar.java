package org.mwg.core.task;

import org.mwg.task.Action;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;

class ActionAddToVar implements Action {

    private final String _name;

    ActionAddToVar(final String p_name) {
        if (p_name == null) {
            throw new RuntimeException("p_name should not be null");
        }
        this._name = p_name;
    }

    @Override
    public void eval(final TaskContext ctx) {
        final TaskResult previousResult = ctx.result();
        if (ctx.isGlobal(_name)) {
            //TODO refactor this
            ctx.addToGlobalVariable(ctx.template(_name), previousResult);
        } else {
            ctx.addToVariable(ctx.template(_name), previousResult);
        }
        ctx.continueTask();
    }

    @Override
    public String toString() {
        return "addToVar(\'" + _name + "\')";
    }

}
