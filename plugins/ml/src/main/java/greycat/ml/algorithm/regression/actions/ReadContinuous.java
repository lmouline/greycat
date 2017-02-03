package greycat.ml.algorithm.regression.actions;

import greycat.Callback;
import greycat.Constants;
import greycat.internal.task.CoreActions;
import greycat.internal.task.TaskHelper;
import greycat.ml.algorithm.regression.PolynomialNode;
import greycat.task.*;
import greycat.task.Action;
import greycat.task.Task;
import greycat.task.TaskContext;
import greycat.task.TaskResult;

public class ReadContinuous implements Action {

    public final static String NAME = "readContinuous";
    private final Task polyTask;
    private final String _relName;

    public ReadContinuous(final String relName) {
        if (relName == null) {
            throw new RuntimeException("name should not be null");
        }
        this._relName = relName;

        polyTask = Tasks.newTask()
                .then(CoreActions.traverse(relName))
                .then(CoreActions.attribute(PolynomialNode.VALUE));
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
