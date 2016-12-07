package org.mwg.core.task;

import org.mwg.Callback;
import org.mwg.plugin.SchedulerAffinity;
import org.mwg.task.Action;
import org.mwg.task.Task;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;

import java.util.concurrent.atomic.AtomicInteger;

class CF_ActionMap implements Action {

    private final Task[] _subTasks;

    CF_ActionMap(final Task... p_subTasks) {
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

    public String serialize() {
        //todo dirty version, don t manage DAG
        StringBuilder res = new StringBuilder();
        res.append("map(");
        for(int i=0;i<_subTasks.length;i++) {
            res.append(_subTasks[i].toString());
            if(i<_subTasks.length - 1) {
                res.append(",  ");
            }
        }
        res.append(")");
        return res.toString();
    }

    @Override
    public String toString() {
        return "map()";
    }

}
