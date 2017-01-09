package org.mwg.core.task;

import org.mwg.Callback;
import org.mwg.Constants;
import org.mwg.DeferCounter;
import org.mwg.plugin.Job;
import org.mwg.plugin.SchedulerAffinity;
import org.mwg.task.Action;
import org.mwg.task.Task;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;

import java.util.Map;

class CF_ActionMapReducePar extends CF_Action {

    private final Task[] _subTasks;
    private final boolean _flat;

    CF_ActionMapReducePar(final boolean flat, final Task... p_subTasks) {
        super();
        _subTasks = p_subTasks;
        _flat = flat;
    }

    @Override
    public void eval(final TaskContext ctx) {
        final TaskResult previous = ctx.result();
        final TaskResult next = ctx.newResult();
        final int subTasksSize = _subTasks.length;
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
        if (_flat) {
            final TaskResult nextFlat = ctx.newResult();
            for (int i = 0; i < next.size(); i++) {
                Object loop = nextFlat.get(i);
                if (loop instanceof CoreTaskResult) {
                    CoreTaskResult casted = (CoreTaskResult) loop;
                    for (int j = 0; j < casted.size(); j++) {
                        final Object loop2 = casted.get(i);
                        if (loop2 != null) {
                            next.add(loop2);
                        }
                    }
                }
            }
        }
        waiter.then(new Job() {
            @Override
            public void run() {
                if (exceptionDuringTask[0] != null) {
                    ctx.endTask(next, exceptionDuringTask[0]);
                } else {
                    ctx.continueWith(next);
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
        if (_flat) {
            builder.append(ActionNames.FLAT_MAP_REDUCE_PAR);
        } else {
            builder.append(ActionNames.MAP_REDUCE_PAR);
        }
        builder.append(Constants.TASK_PARAM_OPEN);
        for (int i = 0; i < _subTasks.length; i++) {
            if (i != 0) {
                builder.append(Constants.TASK_PARAM_SEP);
            }
            final CoreTask castedAction = (CoreTask) _subTasks[i];
            final int castedActionHash = castedAction.hashCode();
            if (dagIDS == null || !dagIDS.containsKey(castedActionHash)) {
                castedAction.serialize(builder, dagIDS);
            } else {
                builder.append("" + dagIDS.get(castedActionHash));
            }
        }
        builder.append(Constants.TASK_PARAM_CLOSE);
    }

}
