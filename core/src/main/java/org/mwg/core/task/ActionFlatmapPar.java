package org.mwg.core.task;

import org.mwg.Callback;
import org.mwg.DeferCounter;
import org.mwg.plugin.AbstractTaskAction;
import org.mwg.plugin.Job;
import org.mwg.plugin.SchedulerAffinity;
import org.mwg.task.Task;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;
import org.mwg.task.TaskResultIterator;

class ActionFlatmapPar extends AbstractTaskAction {

    private final Task _subTask;

    ActionFlatmapPar(final Task p_subTask) {
        super();
        _subTask = p_subTask;
    }

    @Override
    public void eval(final TaskContext context) {
        final TaskResult previousResult = context.result();
        final TaskResult finalResult = context.wrap(null);
        final TaskResultIterator it = previousResult.iterator();
        final int previousSize = previousResult.size();
        if (previousSize == -1) {
            throw new RuntimeException("Foreach on non array structure are not supported yet!");
        }
        finalResult.allocate(previousSize);
        final DeferCounter waiter = context.graph().newCounter(previousSize);


        /*
        Object loop = it.next();
        while (loop != null) {
            final TaskResult loopResult = context.wrap(loop);
            _subTask.executeFrom(context, loopResult, SchedulerAffinity.ANY_LOCAL_THREAD, new Callback<TaskResult>() {
                @Override
                public void on(TaskResult result) {
                    if (result != null) {
                        for (int i = 0; i < result.size(); i++) {
                            finalResult.add(result.get(i));
                        }
                    }
                    loopResult.free();
                    waiter.count();
                }
            });
            loop = it.next();
        }*/


        waiter.then(new Job() {
            @Override
            public void run() {
                context.continueWith(finalResult);
            }
        });
    }

    @Override
    public String toString() {
        return "flatMapPar()";
    }

}
