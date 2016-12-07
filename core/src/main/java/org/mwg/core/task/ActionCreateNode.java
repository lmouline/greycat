package org.mwg.core.task;

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
    public String toString() {
        if (_typeNode != null) {
            return "createTypedNode(\'" + _typeNode + "\')";
        } else {
            return "createNode()";
        }
    }

}
