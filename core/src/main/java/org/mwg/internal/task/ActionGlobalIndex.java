package org.mwg.internal.task;

import org.mwg.Callback;
import org.mwg.Constants;
import org.mwg.NodeIndex;
import org.mwg.task.Action;
import org.mwg.task.TaskContext;

class ActionGlobalIndex implements Action {

    private final String _name;

    ActionGlobalIndex(final String p_indexName) {
        if (p_indexName == null) {
            throw new RuntimeException("indexName should not be null");
        }
        _name = p_indexName;
    }

    @Override
    public void eval(final TaskContext ctx) {
        final String name = ctx.template(_name);
        ctx.graph().indexIfExists(ctx.world(), ctx.time(), name, new Callback<NodeIndex>() {
            @Override
            public void on(NodeIndex resolvedIndex) {
                if (resolvedIndex != null) {
                    ctx.continueWith(ctx.wrap(resolvedIndex));
                } else {
                    ctx.continueWith(ctx.newResult());
                }
            }
        });
    }

    @Override
    public void serialize(StringBuilder builder) {
        builder.append(CoreActionNames.GLOBAL_INDEX);
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
