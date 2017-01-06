package org.mwg.core.task;

import org.mwg.Constants;
import org.mwg.task.Action;
import org.mwg.task.TaskContext;

class ActionDeclareVar implements Action {

    private final String _name;
    private final boolean _isGlobal;

    ActionDeclareVar(final boolean isGlobal, final String p_name) {
        if (p_name == null) {
            throw new RuntimeException("name should not be null");
        }
        this._name = p_name;
        this._isGlobal = isGlobal;
    }

    @Override
    public void eval(final TaskContext ctx) {
        if(_isGlobal){
            ctx.setGlobalVariable(ctx.template(_name), Actions.emptyResult());
        } else {
            ctx.declareVariable(ctx.template(_name));
        }
        ctx.continueTask();
    }

    @Override
    public void serialize(StringBuilder builder) {
        if(_isGlobal){
            builder.append(ActionNames.DECLARE_GLOBAL_VAR);
        } else {
            builder.append(ActionNames.DECLARE_VAR);
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
