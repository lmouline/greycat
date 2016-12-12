package org.mwg.core.task;

import org.mwg.Callback;
import org.mwg.DeferCounter;
import org.mwg.plugin.Job;
import org.mwg.plugin.SchedulerAffinity;
import org.mwg.task.*;
import org.mwg.utility.Tuple;

class CF_ActionFlatMapPar implements Action {

    private final Task _subTask;

    CF_ActionFlatMapPar(final Task p_subTask) {
        _subTask = p_subTask;
    }

    @Override
    public void eval(final TaskContext ctx) {
        final TaskResult previousResult = ctx.result();
        final TaskResult finalResult = ctx.wrap(null);
        final TaskResultIterator it = previousResult.iterator();
        final int previousSize = previousResult.size();
        if (previousSize == -1) {
            throw new RuntimeException("Foreach on non array structure are not supported yet!");
        }
        finalResult.allocate(previousSize);
        final DeferCounter waiter = ctx.graph().newCounter(previousSize);
        final Job[] dequeueJob = new Job[1];
        dequeueJob[0] = new Job() {
            @Override
            public void run() {
                final Tuple<Integer, Object> loop = it.nextWithIndex();
                if (loop != null) {
                    _subTask.executeFromUsing(ctx, ctx.wrap(loop.right()), SchedulerAffinity.ANY_LOCAL_THREAD, new Callback<TaskContext>() {
                        @Override
                        public void on(TaskContext result) {
                            result.defineVariable("i", loop.left());
                        }
                    }, new Callback<TaskResult>() {
                        @Override
                        public void on(TaskResult result) {
                            if (result != null) {
                                for (int i = 0; i < result.size(); i++) {
                                    finalResult.add(result.get(i));
                                }
                            }
                            waiter.count();
                            dequeueJob[0].run();
                        }
                    });
                }
            }
        };
        final int nbThread = ctx.graph().scheduler().workers();
        for (int i = 0; i < nbThread; i++) {
            dequeueJob[0].run();
        }
        waiter.then(new Job() {
            @Override
            public void run() {
                ctx.continueWith(finalResult);
            }
        });
    }

    @Override
    public String toString() {
        return "flatMapPar()";
    }

    @Override
    public void serialize(StringBuilder builder) {
        throw new RuntimeException("Not managed yet!");
    }

}

