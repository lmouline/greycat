package org.mwg.core.task;

import org.mwg.task.Action;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;

class ActionReadVar implements Action {

    private final String _name;
    private final int _index;

    ActionReadVar(String p_name) {
        int indexEnd = -1;
        int indexStart = -1;
        int cursor = p_name.length() - 1;
        while (cursor > 0) {
            char c = p_name.charAt(cursor);
            if (c == ']') {
                indexEnd = cursor;
            } else if (c == '[') {
                indexStart = cursor + 1;
            }
            cursor--;
        }
        if (indexEnd != -1 && indexStart != -1) {
            _index = TaskHelper.parseInt(p_name.substring(indexStart, indexEnd));
            _name = p_name.substring(0, indexStart - 1);
        } else {
            _index = -1;
            _name = p_name;
        }
    }

    @Override
    public void eval(final TaskContext ctx) {
        final String evaluatedName = ctx.template(_name);
        TaskResult varResult;
        if (_index != -1) {
            varResult = ctx.wrap(ctx.variable(evaluatedName).get(_index));
        } else {
            varResult = ctx.variable(evaluatedName);
        }
        if (varResult != null) {
            varResult = varResult.clone();
        }
        ctx.continueWith(varResult);
    }

    @Override
    public String toString() {
        if (_index != -1) {
            return "readVar(\'" + _name + "\'[" + _index + "])";
        } else {
            return "readVar(\'" + _name + "\')";
        }
    }

}
