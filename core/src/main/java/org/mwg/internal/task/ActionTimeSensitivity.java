package org.mwg.internal.task;

import org.mwg.Constants;
import org.mwg.Node;
import org.mwg.base.BaseNode;
import org.mwg.task.Action;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;

class ActionTimeSensitivity implements Action {

    private final long _delta;
    private final long _offset;

    ActionTimeSensitivity(final long delta, final long offset) {
        this._delta = delta;
        this._offset = offset;
    }

    @Override
    public void eval(final TaskContext ctx) {
        final TaskResult previousResult = ctx.result();
        for (int i = 0; i < previousResult.size(); i++) {
            Object loopObj = previousResult.get(i);
            if (loopObj instanceof BaseNode) {
                Node loopNode = (Node) loopObj;
                loopNode.setTimeSensitivity(_delta, _offset);
            }
        }
        ctx.continueTask();
    }

    @Override
    public void serialize(StringBuilder builder) {
        builder.append(CoreActionNames.SET_ATTRIBUTE);
        builder.append(Constants.TASK_PARAM_OPEN);
        builder.append(_delta);
        builder.append(Constants.TASK_PARAM_SEP);
        builder.append(_offset);
        builder.append(Constants.TASK_PARAM_CLOSE);
    }

    @Override
    public String toString() {
        final StringBuilder res = new StringBuilder();
        serialize(res);
        return res.toString();
    }


}

