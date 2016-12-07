package org.mwg.core.task;

import org.mwg.Constants;
import org.mwg.Node;
import org.mwg.Type;
import org.mwg.base.BaseNode;
import org.mwg.task.Action;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;

class ActionSetAttribute implements Action {

    private final String _relationName;
    private final String _variableNameToSet;
    private final byte _propertyType;
    private final boolean _force;

    ActionSetAttribute(final String relationName, final byte propertyType, final String variableNameToSet, final boolean force) {
        this._relationName = relationName;
        this._variableNameToSet = variableNameToSet;
        this._propertyType = propertyType;
        this._force = force;
    }

    @Override
    public void eval(final TaskContext ctx) {
        final TaskResult previousResult = ctx.result();
        final String flatRelationName = ctx.template(_relationName);
        if (previousResult != null) {
            Object toSet;
            Object templateBased = ctx.template(this._variableNameToSet);
            switch (_propertyType) {
                case Type.BOOL:
                    toSet = parseBoolean(templateBased.toString());
                    break;
                case Type.INT:
                    toSet = TaskHelper.parseInt(templateBased.toString());
                    break;
                case Type.DOUBLE:
                    toSet = Double.parseDouble(templateBased.toString());
                    break;
                case Type.LONG:
                    toSet = Long.parseLong(templateBased.toString());
                    break;
                default:
                    toSet = templateBased;
            }
            for (int i = 0; i < previousResult.size(); i++) {
                Object loopObj = previousResult.get(i);
                if (loopObj instanceof BaseNode) {
                    Node loopNode = (Node) loopObj;
                    if(_force){
                        loopNode.forceSet(flatRelationName, _propertyType, toSet);
                    } else {
                        loopNode.set(flatRelationName, _propertyType, toSet);
                    }
                }
            }
        }
        ctx.continueTask();
    }

    @Override
    public String toString() {
        if(_force){
            return "forceSet(\'" + _relationName + "\'" + Constants.QUERY_SEP + "\'" + _propertyType + "\'" + Constants.QUERY_SEP + "\'" + _variableNameToSet + "\')";
        } else {
            return "setAttribute(\'" + _relationName + "\'" + Constants.QUERY_SEP + "\'" + _propertyType + "\'" + Constants.QUERY_SEP + "\'" + _variableNameToSet + "\')";
        }
    }

    private boolean parseBoolean(String booleanValue) {
        final String lower = booleanValue.toLowerCase();
        return (lower.equals("true") || lower.equals("1"));
    }

}
