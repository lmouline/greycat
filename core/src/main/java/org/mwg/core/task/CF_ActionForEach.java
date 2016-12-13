package org.mwg.core.task;

import org.mwg.Callback;
import org.mwg.Constants;
import org.mwg.plugin.SchedulerAffinity;
import org.mwg.task.*;
import org.mwg.utility.Tuple;

import java.util.Map;

class CF_ActionForEach extends CF_Action {

    private final Task _subTask;

    CF_ActionForEach(final Task p_subTask) {
        super();
        _subTask = p_subTask;
    }

    @Override
    public void eval(final TaskContext ctx) {
        final CF_ActionForEach selfPointer = this;
        final TaskResult previousResult = ctx.result();
        if (previousResult == null) {
            ctx.continueTask();
        } else {
            final TaskResultIterator it = previousResult.iterator();
            final Callback[] recursiveAction = new Callback[1];
            recursiveAction[0] = new Callback<TaskResult>() {
                @Override
                public void on(final TaskResult res) {
                    //we don't keep result
                    if (res != null) {
                        res.free();
                    }
                    final Tuple<Integer, Object> nextResult = it.nextWithIndex();
                    if (nextResult == null) {
                        ctx.continueTask();
                    } else {
                        selfPointer._subTask.executeFromUsing(ctx, ctx.wrap(nextResult.right()), SchedulerAffinity.SAME_THREAD, new Callback<TaskContext>() {
                            @Override
                            public void on(TaskContext result) {
                                result.defineVariable("i", nextResult.left());
                            }
                        }, recursiveAction[0]);
                    }
                }
            };
            final Tuple<Integer, Object> nextRes = it.nextWithIndex();
            if (nextRes != null) {
                _subTask.executeFromUsing(ctx, ctx.wrap(nextRes.right()), SchedulerAffinity.SAME_THREAD, new Callback<TaskContext>() {
                    @Override
                    public void on(TaskContext result) {
                        result.defineVariable("i", nextRes.left());
                    }
                }, recursiveAction[0]);
            } else {
                ctx.continueTask();
            }
        }
    }

    @Override
    public Task[] children() {
        Task[] children_tasks = new Task[1];
        children_tasks[0] = _subTask;
        return children_tasks;
    }

    @Override
    public void cf_serialize(StringBuilder builder, Map<Integer, Integer> counters) {
        builder.append(ActionNames.FOR_EACH);
        builder.append(Constants.TASK_PARAM_OPEN);
        CoreTask castedSub = (CoreTask) _subTask;
        if (counters != null && counters.get(castedSub.hashCode()) == 1) {
            castedSub.serialize(builder, counters);
        }
        builder.append(Constants.TASK_PARAM_CLOSE);
    }

}
