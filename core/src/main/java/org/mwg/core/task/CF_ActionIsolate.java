package org.mwg.core.task;

import org.mwg.Callback;
import org.mwg.Constants;
import org.mwg.plugin.SchedulerAffinity;
import org.mwg.task.Action;
import org.mwg.task.Task;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;

import java.util.Map;

class CF_ActionIsolate extends CF_Action {

    private final Task _subTask;

    CF_ActionIsolate(final Task p_subTask) {
        super();
        if (p_subTask == null) {
            throw new RuntimeException("subTask should not be null");
        }
        _subTask = p_subTask;
    }

    @Override
    public void eval(final TaskContext ctx) {
        final TaskResult previous = ctx.result();
        _subTask.executeFrom(ctx, previous, SchedulerAffinity.SAME_THREAD, new Callback<TaskResult>() {
            @Override
            public void on(TaskResult subTaskResult) {
                if (subTaskResult != null) {
                    subTaskResult.free();
                }
                ctx.continueWith(previous);
            }
        });
    }

    @Override
    public Task[] children() {
        Task[] children_tasks = new Task[1];
        children_tasks[0] = _subTask;
        return children_tasks;
    }

    @Override
    public void cf_serialize(StringBuilder builder, Map<Integer, Integer> counters) {
        builder.append(ActionNames.ISOLATE);
        builder.append(Constants.TASK_PARAM_OPEN);
        CoreTask castedSub = (CoreTask) _subTask;
        if (counters != null && counters.get(castedSub.hashCode()) == 1) {
            castedSub.serialize(builder, counters);
        }
        builder.append(Constants.TASK_PARAM_CLOSE);
    }

}
