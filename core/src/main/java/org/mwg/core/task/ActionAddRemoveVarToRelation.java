package org.mwg.core.task;

import org.mwg.Node;
import org.mwg.base.BaseNode;
import org.mwg.task.Action;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;
import org.mwg.task.TaskResultIterator;

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
    public String toString() {
        StringBuilder attString = new StringBuilder();
        for (int i = 0; i < _attributes.length; i++) {
            attString.append(_attributes[i]);
            if (i < _attributes.length - 1) {
                attString.append(", ");
            }
        }
        if (_isAdd) {
            return "add(" + _name + "," + _varFrom + attString.toString() + ")";
        } else {
            return "remove(" + _name + "," + _varFrom + attString.toString() + ")";
        }
    }

}
