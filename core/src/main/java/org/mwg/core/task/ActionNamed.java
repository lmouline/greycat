package org.mwg.core.task;

import org.mwg.task.Action;
import org.mwg.task.TaskActionFactory;
import org.mwg.task.TaskContext;

class ActionNamed implements Action {

    private final String _name;
    private final String[] _params;

    ActionNamed(final String name, final String... params) {
        this._name = name;
        this._params = params;
    }

    @Override
    public void eval(final TaskContext ctx) {
        final TaskActionFactory actionFactory = ctx.graph().taskAction(this._name);
        if (actionFactory == null) {
            throw new RuntimeException("Unknown task action: " + _params);
        }
        final Action subAction = actionFactory.create(ctx.templates(_params));
        if (subAction != null) {
            subAction.eval(ctx);
        } else {
            ctx.continueTask();
        }
    }

    @Override
    public void serialize(StringBuilder builder) {
        builder.append(_name);
        TaskHelper.serializeStringParams(_params, builder);
    }

    @Override
    public String toString() {
        final StringBuilder res = new StringBuilder();
        serialize(res);
        return res.toString();
    }

}
