package org.mwg.core.task;

import org.mwg.Callback;
import org.mwg.plugin.SchedulerAffinity;
import org.mwg.task.*;

class CF_ActionWhileDo implements Action {

    private final ConditionalFunction _cond;
    private final Task _then;

    CF_ActionWhileDo(final ConditionalFunction p_cond, final Task p_then) {
        this._cond = p_cond;
        this._then = p_then;
    }

    @Override
    public void eval(final TaskContext ctx) {
        final CoreTaskContext coreTaskContext = (CoreTaskContext) ctx;
        final CF_ActionWhileDo selfPointer = this;
        final Callback[] recursiveAction = new Callback[1];
        recursiveAction[0] = new Callback<TaskResult>() {
            @Override
            public void on(final TaskResult res) {
                final TaskResult previous = coreTaskContext._result;
                coreTaskContext._result = res;
                if (_cond.eval(ctx)) {
                    if (previous != null) {
                        previous.free();
                    }
                    selfPointer._then.executeFrom(ctx, ((CoreTaskContext) ctx)._result, SchedulerAffinity.SAME_THREAD, recursiveAction[0]);
                } else {
                    if (previous != null) {
                        previous.free();
                    }
                    ctx.continueWith(res);
                }
            }
        };
        if (_cond.eval(ctx)) {
            _then.executeFrom(ctx, coreTaskContext._result, SchedulerAffinity.SAME_THREAD, recursiveAction[0]);
        } else {
            ctx.continueTask();
        }
    }

    @Override
    public void serialize(StringBuilder builder) {
        throw new RuntimeException("Not managed yet!");
    }

    @Override
    public String toString() {
        return "whileDo()";
    }
}
