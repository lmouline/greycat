package org.mwg.internal.task;

import org.mwg.Callback;
import org.mwg.Constants;
import org.mwg.DeferCounter;
import org.mwg.plugin.Job;
import org.mwg.plugin.SchedulerAffinity;
import org.mwg.task.Task;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;

import java.util.Map;

class CF_PipePar extends CF_Action {

    private final Task[] _subTasks;

    CF_PipePar(final Task... p_subTasks) {
        super();
        _subTasks = p_subTasks;
    }

    @Override
    public void eval(final TaskContext ctx) {
        final TaskResult previous = ctx.result();
        final int subTasksSize = _subTasks.length;
        final TaskResult next = ctx.newResult();
        next.allocate(subTasksSize);
        final DeferCounter waiter = ctx.graph().newCounter(subTasksSize);
        final Exception[] exceptionDuringTask = new Exception[1];
        exceptionDuringTask[0] = null;
        for (int i = 0; i < subTasksSize; i++) {
            int finalI = i;
            _subTasks[i].executeFrom(ctx, previous, SchedulerAffinity.ANY_LOCAL_THREAD, new Callback<TaskResult>() {
                @Override
                public void on(TaskResult subTaskResult) {
                    if (subTaskResult != null) {
                        if (subTaskResult.output() != null) {
                            ctx.append(subTaskResult.output());
                        }
                        if (subTaskResult.exception() != null) {
                            exceptionDuringTask[0] = subTaskResult.exception();
                        }
                    }
                    next.set(finalI, subTaskResult);
                    waiter.count();
                }
            });
        }
        waiter.then(new Job() {
            @Override
            public void run() {
                TaskResult endResult = next;
                if (subTasksSize == 1) {
                    endResult = (TaskResult) next.get(0);
                }
                if (exceptionDuringTask[0] != null) {
                    ctx.endTask(endResult, exceptionDuringTask[0]);
                } else {
                    ctx.continueWith(endResult);
                }
            }
        });
    }

    @Override
    public Task[] children() {
        return _subTasks;
    }

    @Override
    public void cf_serialize(StringBuilder builder, Map<Integer, Integer> dagIDS) {
        builder.append(CoreActionNames.PIPE_PAR);
        builder.append(Constants.TASK_PARAM_OPEN);
        for (int i = 0; i < _subTasks.length; i++) {
            if (i != 0) {
                builder.append(Constants.TASK_PARAM_SEP);
            }
            final CoreTask castedAction = (CoreTask) _subTasks[i];
            final int castedActionHash = castedAction.hashCode();
            if (dagIDS == null || !dagIDS.containsKey(castedActionHash)) {
                builder.append(Constants.SUB_TASK_OPEN);
                castedAction.serialize(builder, dagIDS);
                builder.append(Constants.SUB_TASK_CLOSE);
            } else {
                builder.append("" + dagIDS.get(castedActionHash));
            }
        }
        builder.append(Constants.TASK_PARAM_CLOSE);
    }

}
