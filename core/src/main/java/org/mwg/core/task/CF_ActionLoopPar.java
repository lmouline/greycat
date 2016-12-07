package org.mwg.core.task;

import org.mwg.Callback;
import org.mwg.DeferCounter;
import org.mwg.plugin.Job;
import org.mwg.plugin.SchedulerAffinity;
import org.mwg.task.Action;
import org.mwg.task.Task;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;

class CF_ActionLoopPar implements Action {

    private final Task _subTask;

    private final String _lower;
    private final String _upper;

    CF_ActionLoopPar(final String p_lower, final String p_upper, final Task p_subTask) {
        this._subTask = p_subTask;
        this._lower = p_lower;
        this._upper = p_upper;
    }

    @Override
    public void eval(final TaskContext ctx) {
        final String lowerString = ctx.template(_lower);
        final String upperString = ctx.template(_upper);
        final int lower = (int) Double.parseDouble(ctx.template(lowerString));
        final int upper = (int) Double.parseDouble(ctx.template(upperString));
        final TaskResult previous = ctx.result();
        final TaskResult next = ctx.newResult();
        if ((upper - lower) > 0) {
            DeferCounter waiter = ctx.graph().newCounter((upper - lower) + 1);
            for (int i = lower; i <= upper; i++) {
                final int finalI = i;
                _subTask.executeFromUsing(ctx, previous, SchedulerAffinity.ANY_LOCAL_THREAD, new Callback<TaskContext>() {
                    @Override
                    public void on(TaskContext result) {
                        result.defineVariable("i", finalI);
                    }
                }, new Callback<TaskResult>() {
                    @Override
                    public void on(TaskResult result) {
                        if (result != null && result.size() > 0) {
                            for (int i = 0; i < result.size(); i++) {
                                next.add(result.get(i));
                            }
                        }
                        waiter.count();
                    }
                });
            }
            waiter.then(new Job() {
                @Override
                public void run() {
                    ctx.continueWith(next);
                }
            });
        } else {
            ctx.continueWith(next);
        }
    }

    @Override
    public String toString() {
        return "loopPar(\'" + _lower + "\',\'" + _upper + "\')";
    }

}
