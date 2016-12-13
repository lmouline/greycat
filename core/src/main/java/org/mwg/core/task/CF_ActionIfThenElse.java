package org.mwg.core.task;

import org.mwg.Callback;
import org.mwg.Constants;
import org.mwg.plugin.SchedulerAffinity;
import org.mwg.task.*;

import java.util.Map;

class CF_ActionIfThenElse extends CF_Action {

    private ConditionalFunction _condition;
    private org.mwg.task.Task _thenSub;
    private org.mwg.task.Task _elseSub;
    private String _conditionalScript;

    CF_ActionIfThenElse(final ConditionalFunction cond, final org.mwg.task.Task p_thenSub, final org.mwg.task.Task p_elseSub, final String conditionalScript) {
        super();
        if (cond == null) {
            throw new RuntimeException("condition should not be null");
        }
        if (p_thenSub == null) {
            throw new RuntimeException("thenSub should not be null");
        }
        if (p_elseSub == null) {
            throw new RuntimeException("elseSub should not be null");
        }
        this._conditionalScript = conditionalScript;
        this._condition = cond;
        this._thenSub = p_thenSub;
        this._elseSub = p_elseSub;
    }

    @Override
    public void eval(final TaskContext ctx) {
        if (_condition.eval(ctx)) {
            _thenSub.executeFrom(ctx, ctx.result(), SchedulerAffinity.SAME_THREAD, new Callback<TaskResult>() {
                @Override
                public void on(TaskResult res) {
                    ctx.continueWith(res);
                }
            });
        } else {
            _elseSub.executeFrom(ctx, ctx.result(), SchedulerAffinity.SAME_THREAD, new Callback<TaskResult>() {
                @Override
                public void on(TaskResult res) {
                    ctx.continueWith(res);
                }
            });
        }
    }

    @Override
    public Task[] children() {
        Task[] children_tasks = new Task[2];
        children_tasks[0] = _thenSub;
        children_tasks[1] = _elseSub;
        return children_tasks;
    }

    @Override
    public void cf_serialize(StringBuilder builder, Map<Integer, Integer> counters) {
        if (_conditionalScript == null) {
            throw new RuntimeException("Closure is not serializable, please use Script version instead!");
        }
        builder.append(ActionNames.IF_THEN);
        builder.append(Constants.TASK_PARAM_OPEN);
        TaskHelper.serializeString(_conditionalScript, builder);
        builder.append(Constants.TASK_PARAM_SEP);
        CoreTask castedSubThen = (CoreTask) _thenSub;
        if (counters != null && counters.get(castedSubThen.hashCode()) == 1) {
            castedSubThen.serialize(builder, counters);
        }
        builder.append(Constants.TASK_PARAM_SEP);
        CoreTask castedSubElse = (CoreTask) _elseSub;
        if (counters != null && counters.get(castedSubElse.hashCode()) == 1) {
            castedSubElse.serialize(builder, counters);
        }
        builder.append(Constants.TASK_PARAM_CLOSE);
    }

}
