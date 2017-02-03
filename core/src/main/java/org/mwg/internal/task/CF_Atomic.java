package org.mwg.internal.task;

import org.mwg.Callback;
import org.mwg.Constants;
import org.mwg.Node;
import org.mwg.base.BaseNode;
import org.mwg.plugin.Resolver;
import org.mwg.plugin.SchedulerAffinity;
import org.mwg.task.Task;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CF_Atomic extends CF_Action {

    private final String[] _variables;
    private final Task _subTask;

    CF_Atomic(final Task p_subTask, final String... variables) {
        super();
        this._subTask = p_subTask;
        this._variables = variables;
    }

    @Override
    public void eval(final TaskContext ctx) {
        final List<TaskResult> collected = new ArrayList<TaskResult>();
        for (int i = 0; i < _variables.length; i++) {
            String varName = _variables[i];
            TaskResult resolved;
            if (varName.equals("result")) {
                resolved = ctx.result();
            } else {
                resolved = ctx.variable(varName);
            }
            if (resolved != null) {
                collected.add(resolved);
            }
        }
        //acquire lock
        final Resolver resolver = ctx.graph().resolver();
        for (int i = 0; i < collected.size(); i++) {
            TaskResult toLock = collected.get(i);
            for (int j = 0; j < toLock.size(); j++) {
                Object o = toLock.get(j);
                if (o instanceof BaseNode) {
                    resolver.externalLock((Node) o);
                }
            }
        }
        _subTask.executeFrom(ctx, ctx.result(), SchedulerAffinity.SAME_THREAD, new Callback<TaskResult>() {
            @Override
            public void on(TaskResult result) {
                Exception exceptionDuringTask = null;
                if (result != null) {
                    if (result.output() != null) {
                        ctx.append(result.output());
                    }
                    if (result.exception() != null) {
                        exceptionDuringTask = result.exception();
                    }
                }
                for (int i = 0; i < collected.size(); i++) {
                    TaskResult toLock = collected.get(i);
                    for (int j = 0; j < toLock.size(); j++) {
                        Object o = toLock.get(j);
                        if (o instanceof BaseNode) {
                            resolver.externalUnlock((Node) o);
                        }
                    }
                }
                if (exceptionDuringTask != null) {
                    ctx.endTask(result, exceptionDuringTask);
                } else {
                    ctx.continueWith(result);
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
        builder.append(CoreActionNames.ATOMIC);
        builder.append(Constants.TASK_PARAM_OPEN);
        final CoreTask castedAction = (CoreTask) _subTask;
        final int castedActionHash = castedAction.hashCode();
        if (dagIDS == null || !dagIDS.containsKey(castedActionHash)) {
            builder.append(Constants.SUB_TASK_OPEN);
            castedAction.serialize(builder, dagIDS);
            builder.append(Constants.SUB_TASK_CLOSE);
        } else {
            builder.append("" + dagIDS.get(castedActionHash));
        }
        for (int i = 0; i < _variables.length; i++) {
            builder.append(Constants.TASK_PARAM_SEP);
            TaskHelper.serializeString(_variables[i], builder, true);
        }
        builder.append(Constants.TASK_PARAM_CLOSE);
    }


}
