package org.mwg.core.task;

import org.mwg.Constants;
import org.mwg.Node;
import org.mwg.plugin.AbstractNode;
import org.mwg.plugin.AbstractTaskAction;
import org.mwg.plugin.NodeStateCallback;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;


class ActionPropertiesNames extends AbstractTaskAction {
    private final String _flatTypes;

    public ActionPropertiesNames(String flatTypes) {
        super();
        this._flatTypes = flatTypes;
    }

    @Override
    public void eval(TaskContext context) {
        final String templatedAttNames = context.template(_flatTypes);
        final String[] strAttTypes = templatedAttNames.split(Constants.QUERY_SEP + "");

        TaskResult previousResult = context.result();
        TaskResult<String> nextResult = context.newResult();

        for(int i=0;i<previousResult.size();i++) {
            if(previousResult.get(i) instanceof AbstractNode) {
                Node casted = (Node) previousResult.get(i);
                context.graph().resolver().resolveState(casted).each(new NodeStateCallback() {
                    @Override
                    public void on(long attributeKey, byte elemType, Object elem) {
                        for(int idxType = 0;idxType<strAttTypes.length;idxType++) {
                            if(templatedAttNames.length() == 0 ) {
                                nextResult.add(context.graph().resolver().hashToString(attributeKey));
                                return;
                            }
                            if(strAttTypes[idxType].equals(elemType + "")) {
                                nextResult.add(context.graph().resolver().hashToString(attributeKey));
                                return;
                            }
                        }
                    }
                });

                casted.free();
            }
        }

        context.continueWith(nextResult);
    }
}
