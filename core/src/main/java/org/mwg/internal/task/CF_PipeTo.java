package org.mwg.internal.task;

import org.mwg.Callback;
import org.mwg.Constants;
import org.mwg.plugin.SchedulerAffinity;
import org.mwg.task.Task;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;

import java.util.Map;

class CF_PipeTo extends CF_Action {

    private final Task _subTask;
    private final String[] _targets;

    CF_PipeTo(final Task p_subTask, final String... p_targets) {
        super();
        if (p_subTask == null) {
            throw new RuntimeException("subTask should not be null");
        }
        _subTask = p_subTask;
        _targets = p_targets;
    }

    @Override
    public void eval(final TaskContext ctx) {
        final TaskResult previous = ctx.result();
        _subTask.executeFrom(ctx, previous, SchedulerAffinity.SAME_THREAD, new Callback<TaskResult>() {
            @Override
            public void on(TaskResult result) {
                Exception exceptionDuringTask = null;
                if (result != null) {
                    if (result.output() != null) {
                        ctx.append(result.output());
                    }
                    if (result.exception() != null) {
                        exceptionDuringTask = result.exception();
                    }
                    if (_targets != null && _targets.length > 0) {
                        for (int i = 0; i < _targets.length; i++) {
                            ctx.setVariable(_targets[i], result);
                        }
                    } else {
                        result.free();
                    }
                }
                if (exceptionDuringTask != null) {
                    ctx.endTask(previous, exceptionDuringTask);
                } else {
                    ctx.continueWith(previous);
                }
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
    public void cf_serialize(StringBuilder builder, Map<Integer, Integer> dagIDS) {
        builder.append(CoreActionNames.PIPE_TO);
        builder.append(Constants.TASK_PARAM_OPEN);
        final CoreTask castedAction = (CoreTask) _subTask;
        final int castedActionHash = castedAction.hashCode();
        if (dagIDS == null || !dagIDS.containsKey(castedActionHash)) {
            builder.append(Constants.SUB_TASK_OPEN);
            castedAction.serialize(builder, dagIDS);
            builder.append(Constants.SUB_TASK_CLOSE);
        } else {
            builder.append("" + dagIDS.get(castedActionHash));
        }
        if (_targets != null && _targets.length > 0) {
            builder.append(Constants.TASK_PARAM_SEP);
            TaskHelper.serializeStringParams(_targets, builder);
        }
        builder.append(Constants.TASK_PARAM_CLOSE);
    }

}
