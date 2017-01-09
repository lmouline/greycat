package org.mwg.core.task;

import org.mwg.Callback;
import org.mwg.Constants;
import org.mwg.plugin.SchedulerAffinity;
import org.mwg.task.Action;
import org.mwg.task.Task;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

class CF_ActionMapReduce extends CF_Action {

    private final Task[] _subTasks;
    private final boolean _flat;

    CF_ActionMapReduce(final boolean flat, final Task... p_subTasks) {
        super();
        _flat = flat;
        _subTasks = p_subTasks;
    }

    @Override
    public void eval(final TaskContext ctx) {
        final TaskResult previous = ctx.result();
        final AtomicInteger cursor = new AtomicInteger(0);
        final int tasksSize = _subTasks.length;
        final TaskResult next = ctx.newResult();
        final Callback<TaskResult>[] loopcb = new Callback[1];
        loopcb[0] = new Callback<TaskResult>() {
            @Override
            public void on(final TaskResult result) {
                Exception exceptionDuringTask = null;
                final int current = cursor.getAndIncrement();
                if (result != null) {
                    if (_flat) {
                        for (int i = 0; i < result.size(); i++) {
                            final Object loop = result.get(i);
                            if (loop != null) {
                                next.add(loop);
                            }
                        }
                    } else {
                        next.add(result);
                    }
                    if (result.output() != null) {
                        ctx.append(result.output());
                    }
                    if (result.exception() != null) {
                        exceptionDuringTask = result.exception();
                    }
                }
                if (current < tasksSize && exceptionDuringTask == null) {
                    _subTasks[current].executeFrom(ctx, previous, SchedulerAffinity.SAME_THREAD, loopcb[0]);
                } else {
                    //end
                    if (exceptionDuringTask != null) {
                        ctx.endTask(next, exceptionDuringTask);
                    } else {
                        ctx.continueWith(next);
                    }
                }
            }
        };
        final int current = cursor.getAndIncrement();
        if (current < tasksSize) {
            _subTasks[current].executeFrom(ctx, previous, SchedulerAffinity.SAME_THREAD, loopcb[0]);
        } else {
            ctx.continueWith(next);
        }
    }

    @Override
    public Task[] children() {
        return _subTasks;
    }

    @Override
    public void cf_serialize(StringBuilder builder, Map<Integer, Integer> dagIDS) {
        if (_flat) {
            builder.append(ActionNames.FLAT_MAP_REDUCE);
        } else {
            builder.append(ActionNames.MAP_REDUCE);
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
