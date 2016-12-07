package org.mwg.core.task;

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
    public String toString() {
        if(_global){
            return "defineAsGlobalVar(\'" + _name + "\')";
        } else {
            return "defineAsVar(\'" + _name + "\')";
        }
    }

}
