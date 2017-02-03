package org.mwg.internal.task;

import org.mwg.Callback;
import org.mwg.Constants;
import org.mwg.DeferCounter;
import org.mwg.Node;
import org.mwg.base.BaseNode;
import org.mwg.internal.utility.CoreDeferCounter;
import org.mwg.plugin.Job;
import org.mwg.task.Action;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;

class ActionTimepoints implements Action {
    private final String _from;
    private final String _to;

    ActionTimepoints(String from, String to) {
        this._from = from;
        this._to = to;
    }

    @Override
    public void eval(TaskContext ctx) {
        final TaskResult previous = ctx.result();
        String tFrom = ctx.template(_from);
        String tTo = ctx.template(_to);
        long parsedFrom;
        long parsedTo;
        try {
            parsedFrom = Long.parseLong(tFrom);
        } catch (Throwable t) {
            Double d = Double.parseDouble(tFrom);
            parsedFrom = d.longValue();
        }
        try {
            parsedTo = Long.parseLong(tTo);
        } catch (Throwable t) {
            Double d = Double.parseDouble(tTo);
            parsedTo = d.longValue();
        }
        final TaskResult next = ctx.newResult();
        if (previous != null) {
            DeferCounter defer = new CoreDeferCounter(previous.size());
            for (int i = 0; i < previous.size(); i++) {
                if (previous.get(i) instanceof BaseNode) {
                    final Node casted = (Node) previous.get(i);
                    casted.timepoints(parsedFrom, parsedTo, new Callback<long[]>() {
                        @Override
                        public void on(long[] result) {
                            for (int i = 0; i < result.length; i++) {
                                next.add(result[i]);
                            }
                            casted.free();
                            defer.count();
                        }
                    });
                }
            }
            defer.then(new Job() {
                @Override
                public void run() {
                    previous.clear();
                    ctx.continueWith(next);
                }
            });
        } else {
            ctx.continueWith(next);
        }
    }

    @Override
    public void serialize(StringBuilder builder) {
        builder.append(CoreActionNames.TIMEPOINTS);
        builder.append(Constants.TASK_PARAM_OPEN);
        builder.append(Constants.TASK_PARAM_CLOSE);
    }

    @Override
    public String toString() {
        final StringBuilder res = new StringBuilder();
        serialize(res);
        return res.toString();
    }

}
