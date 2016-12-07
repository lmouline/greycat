package org.mwg.ml.algorithm.regression.actions;

import org.mwg.Callback;
import org.mwg.ml.algorithm.regression.PolynomialNode;
import org.mwg.task.Action;
import org.mwg.task.Task;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;

import static org.mwg.core.task.Actions.*;

/**
 * Created by assaad on 07/12/2016.
 */
public class ActionGetContinuous implements Action {

    public final static String NAME = "getContinuous";
    private final Task polyTask;
    private final String _relName;

    public ActionGetContinuous(final String relName) {
        if (relName == null) {
            throw new RuntimeException("name should not be null");
        }
        this._relName = relName;

        polyTask = newTask()
                .then(traverse(relName))
                .then(attribute(PolynomialNode.VALUE));
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
        return NAME + "(\'" + _relName + "\')";
    }

}
