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
public class SetContinuous implements Action {

    public final static String NAME = "setContinuous";
    private final Task polyTask;

    private final String _relName;
    private final String _value;

    public SetContinuous(final String relName, final String c_value) {
        if (relName == null || c_value == null) {
            throw new RuntimeException("name or value should not be null");
        }
        this._relName = relName;
        this._value = c_value;

        polyTask = newTask()
                .then(defineAsVar("origin"))
                .then(traverse(_relName))
                .then(setAttribute(PolynomialNode.VALUE, Type.DOUBLE, _value))
                .then(readVar("origin"));
    }

    @Override
    public void eval(final TaskContext context) {

        polyTask.executeWith(context.graph(), context.result(), new Callback<TaskResult>() {
            @Override
            public void on(TaskResult result) {
                context.continueWith(result);
            }
        });

    }

    @Override
    public String toString() {
        return NAME + "(\'" + _relName + ": " + _value + "\')";
    }

}
