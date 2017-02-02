/**
 * Copyright 2017 The MWG Authors.  All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mwg.internal.task;

import org.mwg.Callback;
import org.mwg.Constants;
import org.mwg.plugin.SchedulerAffinity;
import org.mwg.task.*;
import org.mwg.utility.Tuple;

import java.util.Map;

class CF_ForEach extends CF_Action {

    private final Task _subTask;

    CF_ForEach(final Task p_subTask) {
        super();
        _subTask = p_subTask;
    }

    @Override
    public void eval(final TaskContext ctx) {
        final CF_ForEach selfPointer = this;
        final TaskResult previousResult = ctx.result();
        if (previousResult == null) {
            ctx.continueTask();
        } else {
            final TaskResultIterator it = previousResult.iterator();
            final Callback[] recursiveAction = new Callback[1];
            recursiveAction[0] = new Callback<TaskResult>() {
                @Override
                public void on(final TaskResult res) {
                    //we don't keep result
                    Exception exceptionDuringTask = null;
                    if (res != null) {
                        if (res.output() != null) {
                            ctx.append(res.output());
                        }
                        if (res.exception() != null) {
                            exceptionDuringTask = res.exception();
                        }
                        res.free();
                    }
                    final Tuple<Integer, Object> nextResult = it.nextWithIndex();
                    if (nextResult == null || exceptionDuringTask != null) {
                        if (exceptionDuringTask != null) {
                            ctx.endTask(null, exceptionDuringTask);
                        } else {
                            ctx.continueTask();
                        }
                    } else {
                        selfPointer._subTask.executeFromUsing(ctx, ctx.wrap(nextResult.right()), SchedulerAffinity.SAME_THREAD, new Callback<TaskContext>() {
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
                _subTask.executeFromUsing(ctx, ctx.wrap(nextRes.right()), SchedulerAffinity.SAME_THREAD, new Callback<TaskContext>() {
                    @Override
                    public void on(TaskContext result) {
                        result.defineVariable("i", nextRes.left());
                    }
                }, recursiveAction[0]);
            } else {
                ctx.continueTask();
            }
        }
    }

    @Override
    public final Task[] children() {
        Task[] children_tasks = new Task[1];
        children_tasks[0] = _subTask;
        return children_tasks;
    }

    @Override
    public final void cf_serialize(StringBuilder builder, Map<Integer, Integer> dagIDS) {
        builder.append(CoreActionNames.FOR_EACH);
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
        builder.append(Constants.TASK_PARAM_CLOSE);
    }

}
