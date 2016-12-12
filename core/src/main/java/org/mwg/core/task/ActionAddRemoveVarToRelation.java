package org.mwg.core.task;

import org.mwg.Constants;
import org.mwg.Node;
import org.mwg.base.BaseNode;
import org.mwg.task.*;

class ActionAddRemoveVarToRelation implements Action {

    private final String _name;
    private final String _varFrom;
    private final String[] _attributes;
    private final boolean _isAdd;

    ActionAddRemoveVarToRelation(final boolean isAdd, final String name, final String varFrom, final String... attributes) {
        this._isAdd = isAdd;
        this._name = name;
        this._varFrom = varFrom;
        this._attributes = attributes;
    }

    @Override
    public void eval(final TaskContext ctx) {
        final TaskResult previousResult = ctx.result();
        final TaskResult savedVar = ctx.variable(ctx.template(_varFrom));
        final String[] templatedAttributes = ctx.templates(_attributes);
        if (previousResult != null && savedVar != null) {
            final String relName = ctx.template(_name);
            final TaskResultIterator previousResultIt = previousResult.iterator();
            Object iter = previousResultIt.next();
            while (iter != null) {
                if (iter instanceof BaseNode) {
                    final TaskResultIterator savedVarIt = savedVar.iterator();
                    Object toAddIter = savedVarIt.next();
                    while (toAddIter != null) {
                        if (toAddIter instanceof BaseNode) {
                            final Node castedToAddIter = (Node) toAddIter;
                            final Node castedIter = (Node) iter;
                            if (_isAdd) {
                                castedIter.addToRelation(relName, castedToAddIter, templatedAttributes);
                            } else {
                                castedIter.removeFromRelation(relName, castedToAddIter, templatedAttributes);
                            }
                        }
                        toAddIter = savedVarIt.next();
                    }
                }
                iter = previousResultIt.next();
            }
        }
        ctx.continueTask();
    }

    @Override
    public void serialize(StringBuilder builder) {
        if (_isAdd) {
            builder.append(ActionNames.ADD_VAR_TO_RELATION);
        } else {
            builder.append(ActionNames.REMOVE_VAR_TO_RELATION);
        }
        builder.append(Constants.TASK_PARAM_OPEN);
        TaskHelper.serializeString(_name, builder);
        builder.append(Constants.QUERY_SEP);
        TaskHelper.serializeString(_varFrom, builder);
        builder.append(Constants.QUERY_SEP);
        TaskHelper.serializeStringParams(_attributes, builder);
        builder.append(Constants.TASK_PARAM_CLOSE);
    }

    @Override
    public String toString() {
        final StringBuilder res = new StringBuilder();
        serialize(res);
        return res.toString();
    }

}
