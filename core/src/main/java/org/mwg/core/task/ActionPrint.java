package org.mwg.core.task;

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
            System.out.println(ctx.template(_name));
        } else {
            System.out.print(ctx.template(_name));
        }
        ctx.continueTask();
    }

    @Override
    public String toString() {
        return "print(\'" + _name + "\')";
    }

}
