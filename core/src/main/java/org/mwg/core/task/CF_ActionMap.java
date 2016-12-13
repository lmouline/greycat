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

class CF_ActionMap extends CF_Action {

    private final Task[] _subTasks;

    CF_ActionMap(final Task... p_subTasks) {
        super();
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
                final int current = cursor.getAndIncrement();
                if (result != null) {
                    for (int i = 0; i < result.size(); i++) {
                        final Object loop = result.get(i);
                        if (loop != null) {
                            next.add(loop);
                        }
                    }
                }
                if (current < tasksSize) {
                    _subTasks[current].executeFrom(ctx, previous, SchedulerAffinity.SAME_THREAD, loopcb[0]);
                } else {
                    //end
                    ctx.continueWith(next);
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
    public void cf_serialize(StringBuilder builder, Map<Integer, Integer> counters) {
        builder.append(ActionNames.LOOP);
        builder.append(Constants.TASK_PARAM_OPEN);
        for (int i = 0; i < _subTasks.length; i++) {
            if (i != 0) {
                builder.append(Constants.TASK_PARAM_SEP);
            }
            CoreTask castedSub = (CoreTask) _subTasks[i];
            if (counters != null && counters.get(castedSub.hashCode()) == 1) {
                castedSub.serialize(builder, counters);
            }
        }
        builder.append(Constants.TASK_PARAM_CLOSE);
    }

}
