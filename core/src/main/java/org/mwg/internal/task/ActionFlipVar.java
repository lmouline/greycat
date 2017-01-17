package org.mwg.internal.task;

import org.mwg.Constants;
import org.mwg.task.Action;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;

class ActionFlipVar implements Action {

    private final String _name;

    ActionFlipVar(final String name) {
        if (name == null) {
            throw new RuntimeException("name should not be null");
        }
        this._name = name;
    }

    @Override
    public void eval(final TaskContext ctx) {
        final TaskResult previousGlobalVar = ctx.variable(_name);
        final TaskResult nextResult = previousGlobalVar.clone();
        previousGlobalVar.fillWith(ctx.result());
        ctx.continueWith(nextResult);
    }

    @Override
    public void serialize(StringBuilder builder) {
        builder.append(CoreActionNames.FLIP_VAR);
        builder.append(Constants.TASK_PARAM_OPEN);
        TaskHelper.serializeString(_name, builder, true);
        builder.append(Constants.TASK_PARAM_CLOSE);
    }

    @Override
    public String toString() {
        final StringBuilder res = new StringBuilder();
        serialize(res);
        return res.toString();
    }

}
