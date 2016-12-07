package org.mwg.core.task;

import org.mwg.Node;
import org.mwg.base.BaseNode;
import org.mwg.task.Action;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;

class ActionRemove implements Action {

    private final String _name;

    ActionRemove(final String name) {
        this._name = name;
    }

    @Override
    public void eval(final TaskContext ctx) {
        final TaskResult previousResult = ctx.result();
        if (previousResult != null) {
            final String flatRelationName = ctx.template(_name);
            for (int i = 0; i < previousResult.size(); i++) {
                Object loopObj = previousResult.get(i);
                if (loopObj instanceof BaseNode) {
                    Node loopNode = (Node) loopObj;
                    loopNode.remove(flatRelationName);
                }
            }
        }
        ctx.continueTask();
    }

    @Override
    public String toString() {
        return "remove(\'" + _name + "\')";
    }


}
