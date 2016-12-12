package org.mwg.core.task;

import org.mwg.Constants;
import org.mwg.Node;
import org.mwg.task.Action;
import org.mwg.task.TaskContext;

class ActionCreateNode implements Action {

    private final String _typeNode;

    ActionCreateNode(final String typeNode) {
        this._typeNode = typeNode;
    }

    @Override
    public void eval(final TaskContext ctx) {
        Node newNode;
        if (_typeNode == null) {
            newNode = ctx.graph().newNode(ctx.world(), ctx.time());
        } else {
            String templatedType = ctx.template(_typeNode);
            newNode = ctx.graph().newTypedNode(ctx.world(), ctx.time(), templatedType);
        }
        ctx.continueWith(ctx.wrap(newNode));
    }

    @Override
    public void serialize(StringBuilder builder) {
        if (_typeNode == null) {
            builder.append(ActionNames.CREATE_NODE);
            builder.append(Constants.TASK_PARAM_OPEN);
            builder.append(Constants.TASK_PARAM_CLOSE);
        } else {
            builder.append(ActionNames.CREATE_TYPED_NODE);
            builder.append(Constants.TASK_PARAM_OPEN);
            TaskHelper.serializeString(_typeNode, builder);
            builder.append(Constants.TASK_PARAM_CLOSE);
        }
    }

    @Override
    public String toString() {
        final StringBuilder res = new StringBuilder();
        serialize(res);
        return res.toString();
    }

}
