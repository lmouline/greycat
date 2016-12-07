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

    private final String _relName;

    ActionGetContinuous(final String relName) {
        if (relName == null) {
            throw new RuntimeException("name should not be null");
        }
        this._relName = relName;
    }

    private static final Task polyTask = initPolyTask();

    private static Task initPolyTask() {
        Task result = newTask()
                .then(traverse("{{relname}}"))
                .then(attribute(PolynomialNode.VALUE));
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
        polyTask.executeUsing(polyContext);


    }

    @Override
    public String toString() {
        return "setContinuous(\'" + _relName + "\')";
    }

}
