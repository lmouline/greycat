package org.mwg.ml.algorithm.regression.actions;

import org.mwg.Callback;
import org.mwg.Type;
import org.mwg.ml.algorithm.regression.PolynomialNode;
import org.mwg.task.Action;
import org.mwg.task.Task;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;

import static org.mwg.core.task.Actions.*;
import static org.mwg.core.task.Actions.readVar;

/**
 * Created by assaad on 07/12/2016.
 */
public class ActionSetContinuous implements Action {

    private final String _relName;
    private final String _value;

    ActionSetContinuous(final String relName, final String c_value) {
        if (relName == null || c_value == null) {
            throw new RuntimeException("name or value should not be null");
        }
        this._relName = relName;
        this._value = c_value;
    }

    private static final Task polyTask = initPolyTask();

    private static Task initPolyTask() {
        Task result = newTask()
                .then(defineAsVar("origin"))
                .then(traverse("{{relname}}"))
                .then(setAttribute(PolynomialNode.VALUE, Type.DOUBLE, "{{polyvalue}}"))
                .then(readVar("origin"));
        return result;
    }

    @Override
    public void eval(final TaskContext context) {
        TaskContext polyContext = polyTask.prepare(context.graph(), context.result(), new Callback<TaskResult>() {
            @Override
            public void on(TaskResult result) {
                context.continueTask();
            }
        });

        polyContext.setVariable("relname", _relName);
        polyContext.setVariable("polyvalue", _value);
        polyTask.executeUsing(polyContext);


    }

    @Override
    public String toString() {
        return "setContinuous(\'" + _relName + ": " + _value + "\')";
    }

}
