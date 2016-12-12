package org.mwg.core.task;

import org.mwg.Constants;
import org.mwg.task.Action;
import org.mwg.task.TaskContext;

class ActionDefineAsVar implements Action {

    private final String _name;
    private final boolean _global;

    ActionDefineAsVar(final String p_name, final boolean p_global) {
        if (p_name == null) {
            throw new RuntimeException("name should not be null");
        }
        this._name = p_name;
        this._global = p_global;
    }

    @Override
    public void eval(final TaskContext ctx) {
        if (_global) {
            ctx.setGlobalVariable(ctx.template(_name), ctx.result());
        } else {
            ctx.defineVariable(ctx.template(_name), ctx.result());
        }
        ctx.continueTask();
    }

    @Override
    public void serialize(StringBuilder builder) {
        if (_global) {
            builder.append(ActionNames.DEFINE_AS_GLOBAL_VAR);
        } else {
            builder.append(ActionNames.DEFINE_AS_VAR);
        }
        builder.append(Constants.TASK_PARAM_OPEN);
        TaskHelper.serializeString(_name, builder);
        builder.append(Constants.TASK_PARAM_CLOSE);
    }

    @Override
    public String toString() {
        final StringBuilder res = new StringBuilder();
        serialize(res);
        return res.toString();
    }

}
