package org.mwg.core.task;

import org.mwg.Constants;
import org.mwg.Node;
import org.mwg.plugin.AbstractNode;
import org.mwg.plugin.AbstractTaskAction;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;
import org.mwg.task.TaskResultIterator;

class ActionAddTo extends AbstractTaskAction {

    private final String _relationName;
    private final String _variableNameTarget;

    ActionAddTo(final String relationName, final String variableNameTarget) {
        super();
        this._relationName = relationName;
        this._variableNameTarget = variableNameTarget;
    }

    @Override
    public void eval(final TaskContext context) {
        final TaskResult previousResult = context.result();
        final TaskResult savedVar = context.variable(context.template(_variableNameTarget));
        if (previousResult != null && savedVar != null) {
            final String relName = context.template(_relationName);
            final TaskResultIterator previousResultIt = previousResult.iterator();
            Object iter = previousResultIt.next();
            while (iter != null) {
                if (iter instanceof AbstractNode) {
                    final TaskResultIterator savedVarIt = savedVar.iterator();
                    Object toAddIter = savedVarIt.next();
                    while (toAddIter != null) {
                        if (toAddIter instanceof AbstractNode) {
                            ((AbstractNode) toAddIter).add(relName, (Node) iter);
                        }
                        toAddIter = savedVarIt.next();
                    }
                }
                iter = previousResultIt.next();
            }
        }
        context.continueTask();
    }

    @Override
    public String toString() {
        return "addTo(\'" + _relationName + "\'" + Constants.QUERY_SEP + "\'" + _variableNameTarget + "\')";
    }

}
