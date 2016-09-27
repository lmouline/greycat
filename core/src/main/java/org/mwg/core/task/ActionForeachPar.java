package org.mwg.core.task;

import org.mwg.Callback;
import org.mwg.DeferCounter;
import org.mwg.plugin.AbstractTaskAction;
import org.mwg.plugin.Job;
import org.mwg.plugin.SchedulerAffinity;
import org.mwg.task.*;

class ActionForeachPar extends AbstractTaskAction {

    private final Task _subTask;

    ActionForeachPar(final Task p_subTask) {
        super();
        _subTask = p_subTask;
    }

    @Override
    public void eval(final TaskContext context) {
        final TaskResult previousResult = context.result();
        final TaskResultIterator it = previousResult.iterator();
        final int previousSize = previousResult.size();
        if (previousSize == -1) {
            throw new RuntimeException("Foreach on non array structure are not supported yet!");
        }
        final DeferCounter waiter = context.graph().newCounter(previousSize);
        final Job[] dequeueJob = new Job[1];
        dequeueJob[0] = new Job() {
            @Override
            public void run() {
                final Object loop = it.next();
                if (loop != null) {
                    _subTask.executeFrom(context, context.wrap(loop), SchedulerAffinity.ANY_LOCAL_THREAD, new Callback<TaskResult>() {
                        @Override
                        public void on(TaskResult result) {
                            if (result != null) {
                                result.free();
                            }
                            waiter.count();
                            dequeueJob[0].run();
                        }
                    });
                }
            }
        };
        final int nbThread = context.graph().scheduler().workers();
        for (int i = 0; i < nbThread; i++) {
            dequeueJob[0].run();
        }
        waiter.then(new Job() {
            @Override
            public void run() {
                context.continueTask();
            }
        });
    }

    @Override
    public String toString() {
        return "foreachPar()";
    }

}
