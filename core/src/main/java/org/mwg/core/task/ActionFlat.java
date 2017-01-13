package org.mwg.core.task;

import org.mwg.Constants;
import org.mwg.task.Action;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;

class ActionFlat implements Action {

    ActionFlat() {
    }

    @Override
    public void eval(final TaskContext ctx) {
        TaskResult result = ctx.result();
        if (result == null) {
            ctx.continueTask();
        } else {
            final TaskResult next = ctx.newResult();
            for (int i = 0; i < result.size(); i++) {
                final Object loop = result.get(i);
                if(loop instanceof CoreTaskResult){
                    CoreTaskResult casted = (CoreTaskResult) loop;
                    for (int j = 0; j < casted.size(); j++) {
                        final Object resultLoop = casted.get(j);
                        if (resultLoop != null) {
                            next.add(resultLoop);
                        }
                    }
                } else {
                    if (loop != null) {
                        next.add(loop);
                    }
                }


            }
            ctx.continueWith(next);
        }
    }

    @Override
    public void serialize(StringBuilder builder) {
        builder.append(ActionNames.FLAT);
        builder.append(Constants.TASK_PARAM_OPEN);
        builder.append(Constants.TASK_PARAM_CLOSE);
    }

    @Override
    public String toString() {
        final StringBuilder res = new StringBuilder();
        serialize(res);
        return res.toString();
    }

}
