package org.mwg.core.task;

import org.mwg.Callback;
import org.mwg.Constants;
import org.mwg.plugin.Job;
import org.mwg.plugin.SchedulerAffinity;
import org.mwg.task.*;
import org.mwg.utility.Tuple;

import java.util.Map;

class CF_ActionMap extends CF_Action {

    private final Task _subTask;
    private final boolean _flat;

    CF_ActionMap(final Task p_subTask, final boolean flat) {
        super();
        _flat = flat;
        _subTask = p_subTask;
    }

    @Override
    public void eval(final TaskContext ctx) {
        final CF_ActionMap selfPointer = this;
        final TaskResult previousResult = ctx.result();
        if (previousResult == null) {
            ctx.continueTask();
        } else {
            final TaskResultIterator it = previousResult.iterator();
            final TaskResult finalResult = ctx.newResult();
            finalResult.allocate(previousResult.size());
            final Callback[] recursiveAction = new Callback[1];
            final TaskResult[] loopRes = new TaskResult[1];
            recursiveAction[0] = new Callback<TaskResult>() {
                @Override
                public void on(final TaskResult res) {
                    Exception exceptionDuringTask = null;
                    if (res != null) {
                        if (_flat) {
                            for (int i = 0; i < res.size(); i++) {
                                finalResult.add(res.get(i));
                            }
                        } else {
                            finalResult.add(res);
                        }
                        if (res.output() != null) {
                            ctx.append(res.output());
                        }
                        if (res.exception() != null) {
                            exceptionDuringTask = res.exception();
                        }
                    }
                    loopRes[0].free();
                    final Tuple<Integer, Object> nextResult = it.nextWithIndex();
                    if (nextResult != null) {
                        loopRes[0] = ctx.wrap(nextResult.right());
                    } else {
                        loopRes[0] = null;
                    }
                    if (nextResult == null || exceptionDuringTask != null) {
                        if (exceptionDuringTask != null) {
                            ctx.endTask(finalResult, exceptionDuringTask);
                        } else {
                            ctx.continueWith(finalResult);
                        }
                    } else {
                        selfPointer._subTask.executeFromUsing(ctx, loopRes[0], SchedulerAffinity.SAME_THREAD, new Callback<TaskContext>() {
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
                loopRes[0] = ctx.wrap(nextRes.right());
                ctx.graph().scheduler().dispatch(SchedulerAffinity.SAME_THREAD, new Job() {
                    @Override
                    public void run() {
                        _subTask.executeFromUsing(ctx, loopRes[0], SchedulerAffinity.SAME_THREAD, new Callback<TaskContext>() {
                            @Override
                            public void on(TaskContext result) {
                                result.defineVariable("i", nextRes.left());
                            }
                        }, recursiveAction[0]);
                    }
                });
            } else {
                ctx.continueWith(finalResult);
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
    public void cf_serialize(StringBuilder builder, Map<Integer, Integer> dagIDS) {
        if (_flat) {
            builder.append(ActionNames.FLAT_MAP);
        } else {
            builder.append(ActionNames.MAP);
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
