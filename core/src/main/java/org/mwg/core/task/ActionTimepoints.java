package org.mwg.core.task;

import org.mwg.Callback;
import org.mwg.DeferCounter;
import org.mwg.Node;
import org.mwg.base.BaseNode;
import org.mwg.core.utility.CoreDeferCounter;
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
    public void eval(TaskContext context) {
        final TaskResult previous  = context.result();
        String tFrom = context.template(_from);
        String tTo = context.template(_to);

        long parsedFrom = Long.parseLong(tFrom);
        long parsedTo = Long.parseLong(tTo);

        final TaskResult next = context.newResult();

        if(previous != null) {
            DeferCounter defer = new CoreDeferCounter(previous.size());
            for(int i=0;i<previous.size();i++) {
                if(previous.get(i) instanceof BaseNode) {
                    final Node casted = (Node) previous.get(i);
                    casted.timepoints(parsedFrom, parsedTo, new Callback<long[]>() {
                        @Override
                        public void on(long[] result) {
                            for(int i=0;i<result.length;i++) {
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
                    context.continueWith(next);
                }
            });
        } else {
            context.continueWith(next);
        }





    }
}
