package org.mwg.core.task;

import org.mwg.Callback;
import org.mwg.Constants;
import org.mwg.Node;
import org.mwg.task.Action;
import org.mwg.task.TaskContext;

class ActionLookup implements Action {

    private final String _id;

    ActionLookup(final String p_id) {
        this._id = p_id;
    }

    @Override
    public void eval(final TaskContext ctx) {
        final long idL = Long.parseLong(ctx.template(_id));
        ctx.graph().lookup(ctx.world(), ctx.time(), idL, new Callback<Node>() {
            @Override
            public void on(Node result) {
                ctx.continueWith(ctx.wrap(result));
            }
        });
    }

    @Override
    public void serialize(StringBuilder builder) {
        builder.append(ActionNames.LOOKUP);
        builder.append(Constants.TASK_PARAM_OPEN);
        TaskHelper.serializeString(_id, builder,true);
        builder.append(Constants.TASK_PARAM_CLOSE);
    }

    @Override
    public String toString() {
        final StringBuilder res = new StringBuilder();
        serialize(res);
        return res.toString();
    }

}
