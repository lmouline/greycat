package org.mwg.core.task;

import org.mwg.Callback;
import org.mwg.Constants;
import org.mwg.plugin.SchedulerAffinity;
import org.mwg.task.*;

import java.util.Map;

class CF_ActionIfThen extends CF_Action {

    private ConditionalFunction _condition;
    private org.mwg.task.Task _action;
    private String _conditionalScript;

    CF_ActionIfThen(final ConditionalFunction cond, final org.mwg.task.Task action, final String conditionalScript) {
        super();
        if (cond == null) {
            throw new RuntimeException("condition should not be null");
        }
        if (action == null) {
            throw new RuntimeException("subTask should not be null");
        }
        this._conditionalScript = conditionalScript;
        this._condition = cond;
        this._action = action;
    }

    @Override
    public void eval(final TaskContext ctx) {
        if (_condition.eval(ctx)) {
            _action.executeFrom(ctx, ctx.result(), SchedulerAffinity.SAME_THREAD, new Callback<TaskResult>() {
                @Override
                public void on(TaskResult res) {
                    Exception foundException = null;
                    if (res != null) {
                        if (res.output() != null) {
                            ctx.append(res.output());
                        }
                        if (res.exception() != null) {
                            foundException = res.exception();
                        }
                    }
                    if (foundException != null) {
                        ctx.endTask(res, foundException);
                    } else {
                        ctx.continueWith(res);
                    }
                }
            });
        } else {
            ctx.continueTask();
        }
    }

    @Override
    public Task[] children() {
        Task[] children_tasks = new Task[1];
        children_tasks[0] = _action;
        return children_tasks;
    }

    @Override
    public void cf_serialize(StringBuilder builder, Map<Integer, Integer> dagIDS) {
        if (_conditionalScript == null) {
            throw new RuntimeException("Closure is not serializable, please use Script version instead!");
        }
        builder.append(ActionNames.IF_THEN);
        builder.append(Constants.TASK_PARAM_OPEN);
        TaskHelper.serializeString(_conditionalScript, builder, true);
        builder.append(Constants.TASK_PARAM_SEP);
        final CoreTask castedAction = (CoreTask) _action;
        final int castedActionHash = castedAction.hashCode();
        if (dagIDS == null || !dagIDS.containsKey(castedActionHash)) {
            castedAction.serialize(builder, dagIDS);
        } else {
            builder.append("" + dagIDS.get(castedActionHash));
        }
        builder.append(Constants.TASK_PARAM_CLOSE);
    }

}
