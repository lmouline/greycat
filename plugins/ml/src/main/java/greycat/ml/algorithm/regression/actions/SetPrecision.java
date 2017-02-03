package greycat.ml.algorithm.regression.actions;

import greycat.Constants;
import greycat.task.Action;
import greycat.task.Task;
import greycat.Callback;
import greycat.Type;
import greycat.internal.task.TaskHelper;
import greycat.ml.algorithm.regression.PolynomialNode;
import greycat.task.TaskContext;
import greycat.task.TaskResult;

import static greycat.internal.task.CoreActions.readVar;
import static greycat.task.Tasks.newTask;

public class SetPrecision implements Action {

    public final static String NAME = "setPrecision";
    private final Task polyTask;

    private final String _relName;
    private final String _value;

    public SetPrecision(final String relName, final String c_value) {
        if (relName == null || c_value == null) {
            throw new RuntimeException("name or value should not be null");
        }
        this._relName = relName;
        this._value = c_value;
        polyTask = newTask()
                .then(defineAsVar("origin"))
                .then(traverse(_relName))
                .then(setAttribute(PolynomialNode.PRECISION, Type.DOUBLE, _value))
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
    public void serialize(StringBuilder builder) {
        builder.append(NAME);
        builder.append(Constants.TASK_PARAM_OPEN);
        TaskHelper.serializeString(_relName, builder,true);
        builder.append(Constants.TASK_PARAM_SEP);
        TaskHelper.serializeString(_value, builder,true);
        builder.append(Constants.TASK_PARAM_CLOSE);
    }

    @Override
    public String toString() {
        final StringBuilder res = new StringBuilder();
        serialize(res);
        return res.toString();
    }

}
