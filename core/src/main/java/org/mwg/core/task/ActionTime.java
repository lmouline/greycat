package org.mwg.core.task;

import org.mwg.plugin.AbstractTaskAction;
import org.mwg.task.TaskContext;

class ActionTime extends AbstractTaskAction {

    private final String _varName;

    ActionTime(final String p_varName) {
        super();
        this._varName = p_varName;
    }

    @Override
    public void eval(final TaskContext context) {
        final String flat = context.template(_varName);
        long parsedTime;
        if(flat.contains(".")){
            parsedTime = Long.parseLong(flat);
        } else {
            parsedTime = (long) Double.parseDouble(flat);
        }
        context.setTime(parsedTime);
        context.continueTask();
    }

    @Override
    public String toString() {
        return "setTime(\'" + _varName + "\')";
    }

}

