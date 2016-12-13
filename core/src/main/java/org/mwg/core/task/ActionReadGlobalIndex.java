package org.mwg.core.task;

import org.mwg.Callback;
import org.mwg.Constants;
import org.mwg.Node;
import org.mwg.NodeIndex;
import org.mwg.task.Action;
import org.mwg.task.TaskContext;

class ActionReadGlobalIndex implements Action {

    private final String _name;
    private final String[] _params;

    ActionReadGlobalIndex(final String p_indexName, final String... p_query) {
        if (p_indexName == null) {
            throw new RuntimeException("indexName should not be null");
        }
        if (p_query == null) {
            throw new RuntimeException("query should not be null");
        }
        _name = p_indexName;
        _params = p_query;
    }

    @Override
    public void eval(final TaskContext ctx) {
        final String name = ctx.template(_name);
        final String[] query = ctx.templates(_params);
        ctx.graph().index(ctx.world(), ctx.time(), name, new Callback<NodeIndex>() {
            @Override
            public void on(NodeIndex resolvedIndex) {
                resolvedIndex.find(new Callback<Node[]>() {
                    @Override
                    public void on(Node[] result) {
                        resolvedIndex.free();
                        ctx.continueWith(ctx.wrap(result));
                    }
                }, query);
            }
        });
    }

    @Override
    public void serialize(StringBuilder builder) {
        builder.append(ActionNames.READ_GLOBAL_INDEX);
        builder.append(Constants.TASK_PARAM_OPEN);
        TaskHelper.serializeString(_name, builder);
        builder.append(Constants.TASK_PARAM_SEP);
        TaskHelper.serializeStringParams(_params, builder);
        builder.append(Constants.TASK_PARAM_CLOSE);
    }

    @Override
    public String toString() {
        final StringBuilder res = new StringBuilder();
        serialize(res);
        return res.toString();
    }

}
