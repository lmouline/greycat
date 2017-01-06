package org.mwg.core.task;

import org.mwg.Constants;
import org.mwg.Node;
import org.mwg.base.BaseNode;
import org.mwg.task.Action;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;

import java.util.regex.Pattern;

class ActionWithout implements Action {

    private final String _patternTemplate;
    private final String _name;

    ActionWithout(final String name, final String stringPattern) {
        if (name == null) {
            throw new RuntimeException("name should not be null");
        }
        if (stringPattern == null) {
            throw new RuntimeException("pattern should not be null");
        }
        this._patternTemplate = stringPattern;
        this._name = name;
    }

    @Override
    public void eval(final TaskContext ctx) {
        final Pattern pattern = Pattern.compile(ctx.template(_patternTemplate));
        final TaskResult previous = ctx.result();
        final TaskResult next = ctx.newResult();
        final int previousSize = previous.size();
        for (int i = 0; i < previousSize; i++) {
            final Object obj = previous.get(i);
            if (obj instanceof BaseNode) {
                final Node casted = (Node) obj;
                Object currentName = casted.get(_name);
                if (currentName == null || !pattern.matcher(currentName.toString()).matches()) {
                    next.add(casted.graph().cloneNode(casted));
                }
            } else {
                next.add(obj);
            }
        }
        ctx.continueWith(next);
    }

    @Override
    public void serialize(StringBuilder builder) {
        builder.append(ActionNames.WITHOUT);
        builder.append(Constants.TASK_PARAM_OPEN);
        TaskHelper.serializeString(_name, builder,true);
        builder.append(Constants.TASK_PARAM_SEP);
        TaskHelper.serializeString(_patternTemplate, builder,true);
        builder.append(Constants.TASK_PARAM_CLOSE);
    }

    @Override
    public String toString() {
        final StringBuilder res = new StringBuilder();
        serialize(res);
        return res.toString();
    }

}
