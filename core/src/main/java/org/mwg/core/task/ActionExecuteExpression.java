package org.mwg.core.task;

import org.mwg.Node;
import org.mwg.base.BaseNode;
import org.mwg.core.task.math.CoreMathExpressionEngine;
import org.mwg.core.task.math.MathExpressionEngine;
import org.mwg.task.Action;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;

import java.util.HashMap;
import java.util.Map;

class ActionExecuteExpression implements Action {

    final private MathExpressionEngine _engine;
    final private String _expression;

    ActionExecuteExpression(final String mathExpression) {
        this._expression = mathExpression;
        this._engine = CoreMathExpressionEngine.parse(mathExpression);
    }

    @Override
    public void eval(final TaskContext ctx) {
        final TaskResult previous = ctx.result();
        final TaskResult<Double> next = ctx.newResult();
        final int previousSize = previous.size();
        for (int i = 0; i < previousSize; i++) {
            final Object loop = previous.get(i);
            Map<String, Double> variables = new HashMap<String, Double>();
            variables.put("PI", Math.PI);
            variables.put("TRUE", 1.0);
            variables.put("FALSE", 0.0);
            if (loop instanceof BaseNode) {
                next.add(_engine.eval((Node) loop, ctx, variables));
                ((BaseNode) loop).free();
            } else {
                next.add(_engine.eval(null, ctx, variables));
            }
        }
        //optimization to avoid iteration on previous result for free
        previous.clear();
        ctx.continueWith(next);
    }

    @Override
    public String toString() {
        return "executeExpression(\'" + _expression + "\')";
    }

}
