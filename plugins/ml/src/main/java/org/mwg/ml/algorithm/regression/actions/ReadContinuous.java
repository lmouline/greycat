package org.mwg.ml.algorithm.regression.actions;

import org.mwg.Callback;
import org.mwg.Constants;
import org.mwg.internal.task.TaskHelper;
import org.mwg.ml.algorithm.regression.PolynomialNode;
import org.mwg.task.Action;
import org.mwg.task.Task;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;

import static org.mwg.internal.task.CoreActions.*;
import static org.mwg.task.Tasks.newTask;

public class ReadContinuous implements Action {

    public final static String NAME = "readContinuous";
    private final Task polyTask;
    private final String _relName;

    public ReadContinuous(final String relName) {
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
    public void serialize(StringBuilder builder) {
        builder.append(NAME);
        builder.append(Constants.TASK_PARAM_OPEN);
        TaskHelper.serializeString(_relName, builder, true);
        builder.append(Constants.TASK_PARAM_CLOSE);
    }

    @Override
    public String toString() {
        final StringBuilder res = new StringBuilder();
        serialize(res);
        return res.toString();
    }

}
