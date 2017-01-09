package org.mwg.core.task;

import org.mwg.Callback;
import org.mwg.Constants;
import org.mwg.DeferCounter;
import org.mwg.plugin.Job;
import org.mwg.plugin.SchedulerAffinity;
import org.mwg.task.*;
import org.mwg.utility.Tuple;

import java.util.Map;

class CF_ActionMapPar extends CF_Action {

    private final Task _subTask;
    private final boolean _flat;

    CF_ActionMapPar(final Task p_subTask, final boolean flat) {
        super();
        _flat = flat;
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
        final Exception[] exceptionDuringTask = new Exception[1];
        exceptionDuringTask[0] = null;
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
                                if (_flat) {
                                    for (int i = 0; i < result.size(); i++) {
                                        finalResult.add(result.get(i));
                                    }
                                } else {
                                    finalResult.add(result);
                                }
                                if (result.output() != null) {
                                    ctx.append(result.output());
                                }
                                if (result.exception() != null) {
                                    exceptionDuringTask[0] = result.exception();
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
                if (exceptionDuringTask[0] != null) {
                    ctx.endTask(finalResult, exceptionDuringTask[0]);
                } else {
                    ctx.continueWith(finalResult);
                }
            }
        });
    }

    @Override
    public Task[] children() {
        Task[] children_tasks = new Task[1];
        children_tasks[0] = _subTask;
        return children_tasks;
    }

    @Override
    public void cf_serialize(StringBuilder builder, Map<Integer, Integer> dagIDS) {
        if (_flat) {
            builder.append(ActionNames.FLAT_MAP_PAR);
        } else {
            builder.append(ActionNames.MAP_PAR);
        }
        builder.append(Constants.TASK_PARAM_OPEN);
        final CoreTask castedAction = (CoreTask) _subTask;
        final int castedActionHash = castedAction.hashCode();
        if (dagIDS == null || !dagIDS.containsKey(castedActionHash)) {
            castedAction.serialize(builder, dagIDS);
        } else {
            builder.append("" + dagIDS.get(castedActionHash));
        }
        builder.append(Constants.TASK_PARAM_CLOSE);
    }
}

