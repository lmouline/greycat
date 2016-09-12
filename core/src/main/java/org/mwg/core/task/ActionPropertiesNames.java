package org.mwg.core.task;

import org.mwg.Node;
import org.mwg.plugin.AbstractNode;
import org.mwg.plugin.AbstractTaskAction;
import org.mwg.plugin.NodeStateCallback;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;


class ActionPropertiesNames extends AbstractTaskAction {
    private final byte _type;

    public ActionPropertiesNames(byte type) {
        super();
        this._type = type;
    }

    @Override
    public void eval(TaskContext context) {
        TaskResult previousResult = context.result();
        TaskResult<String> nextResult = context.newResult();

        for(int i=0;i<previousResult.size();i++) {
            if(previousResult.get(i) instanceof AbstractNode) {
                Node casted = (Node) previousResult.get(i);
                context.graph().resolver().resolveState(casted).each(new NodeStateCallback() {
                    @Override
                    public void on(long attributeKey, byte elemType, Object elem) {
                        if (_type == -1 || elemType == _type) {
                            String retrieved = context.graph().resolver().hashToString(attributeKey);
                            if(retrieved != null) {
                                nextResult.add(retrieved);
                            }
                        }

                    }
                });

                casted.free();
            }
        }

        previousResult.clear();
        context.continueWith(nextResult);
    }

    @Override
    public String toString() {
        if(_type == -1) {
            return "properties()";
        }
        return "propertiesWithType(\'" + _type + "\')";
    }
}
