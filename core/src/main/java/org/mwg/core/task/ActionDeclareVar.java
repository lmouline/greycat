package org.mwg.core.task;

import org.mwg.task.Action;
import org.mwg.task.TaskContext;

class ActionDeclareVar implements Action {

    private final String _name;

    ActionDeclareVar(final String p_name) {
        if (p_name == null) {
            throw new RuntimeException("name should not be null");
        }
        this._name = p_name;
    }

    @Override
    public void eval(final TaskContext ctx) {
        ctx.declareVariable(ctx.template(_name));
        ctx.continueTask();
    }

    @Override
    public String toString() {
        return "declareVar(\'" + _name + "\')";
    }

}
