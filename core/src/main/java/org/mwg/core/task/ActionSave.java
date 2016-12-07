package org.mwg.core.task;

import org.mwg.Callback;
import org.mwg.task.Action;
import org.mwg.task.TaskContext;

class ActionSave implements Action {

    @Override
    public void eval(final TaskContext ctx) {
        ctx.graph().save(new Callback<Boolean>() {
            @Override
            public void on(Boolean result) {
                ctx.continueTask();
            }
        });
    }

    @Override
    public String toString() {
        return "save()";
    }

}
