package org.mwg.core.task;

import org.mwg.Node;
import org.mwg.plugin.AbstractNode;
import org.mwg.plugin.AbstractTaskAction;
import org.mwg.plugin.NodeState;
import org.mwg.plugin.NodeStateCallback;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;


class ActionRelations extends AbstractTaskAction {
    private final byte _typeOfRelation;

    ActionRelations(byte typeOfRelations) {
        super();
        this._typeOfRelation = typeOfRelations;
    }

    @Override
    public void eval(TaskContext context) {
        final TaskResult previous = context.result();

        TaskResult<String> result = context.newResult();

        for(int i=0;i<previous.size();i++) {
            if(previous.get(i) instanceof AbstractNode) {
                final Node n = (org.mwg.Node) previous.get(i);
                final NodeState nState= context.graph().resolver().resolveState(n);
                nState.each(new NodeStateCallback() {
                    @Override
                    public void on(long attributeKey, byte elemType, Object elem) {
                        if(elemType == _typeOfRelation) {
                            result.add(context.graph().resolver().hashToString(attributeKey));
                        }
                    }
                });
                n.free();
            }
        }

        previous.clear();
        context.continueWith(result);

    }
}
