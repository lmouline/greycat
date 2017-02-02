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

import java.util.Map;

class CF_IfThenElse extends CF_Action {

    private ConditionalFunction _condition;
    private org.mwg.task.Task _thenSub;
    private org.mwg.task.Task _elseSub;
    private String _conditionalScript;

    CF_IfThenElse(final ConditionalFunction cond, final org.mwg.task.Task p_thenSub, final org.mwg.task.Task p_elseSub, final String conditionalScript) {
        super();
        if (cond == null) {
            throw new RuntimeException("condition should not be null");
        }
        if (p_thenSub == null) {
            throw new RuntimeException("thenSub should not be null");
        }
        if (p_elseSub == null) {
            throw new RuntimeException("elseSub should not be null");
        }
        this._conditionalScript = conditionalScript;
        this._condition = cond;
        this._thenSub = p_thenSub;
        this._elseSub = p_elseSub;
    }

    @Override
    public void eval(final TaskContext ctx) {
        final Task selectedNextTask;
        if (_condition.eval(ctx)) {
            selectedNextTask = _thenSub;
        } else {
            selectedNextTask = _elseSub;
        }
        selectedNextTask.executeFrom(ctx, ctx.result(), SchedulerAffinity.SAME_THREAD, new Callback<TaskResult>() {
            @Override
            public void on(TaskResult res) {
                Exception exceptionDuringTask = null;
                if (res != null) {
                    if (res.output() != null) {
                        ctx.append(res.output());
                    }
                    if (res.exception() != null) {
                        exceptionDuringTask = res.exception();
                    }
                }
                if (exceptionDuringTask != null) {
                    ctx.endTask(res, exceptionDuringTask);
                } else {
                    ctx.continueWith(res);
                }
            }
        });
    }

    @Override
    public Task[] children() {
        Task[] children_tasks = new Task[2];
        children_tasks[0] = _thenSub;
        children_tasks[1] = _elseSub;
        return children_tasks;
    }

    @Override
    public void cf_serialize(StringBuilder builder, Map<Integer, Integer> dagIDS) {
        if (_conditionalScript == null) {
            throw new RuntimeException("Closure is not serializable, please use Script version instead!");
        }
        builder.append(CoreActionNames.IF_THEN_ELSE);
        builder.append(Constants.TASK_PARAM_OPEN);
        TaskHelper.serializeString(_conditionalScript, builder, true);
        builder.append(Constants.TASK_PARAM_SEP);
        CoreTask castedSubThen = (CoreTask) _thenSub;
        int castedSubThenHash = castedSubThen.hashCode();
        if (dagIDS == null || !dagIDS.containsKey(castedSubThenHash)) {
            builder.append(Constants.SUB_TASK_OPEN);
            castedSubThen.serialize(builder, dagIDS);
            builder.append(Constants.SUB_TASK_CLOSE);
        } else {
            builder.append("" + dagIDS.get(castedSubThenHash));
        }
        builder.append(Constants.TASK_PARAM_SEP);
        CoreTask castedSubElse = (CoreTask) _elseSub;
        int castedSubElseHash = castedSubElse.hashCode();
        if (dagIDS == null || !dagIDS.containsKey(castedSubElseHash)) {
            builder.append(Constants.SUB_TASK_OPEN);
            castedSubElse.serialize(builder, dagIDS);
            builder.append(Constants.SUB_TASK_CLOSE);
        } else {
            builder.append("" + dagIDS.get(castedSubElseHash));
        }
        builder.append(Constants.TASK_PARAM_CLOSE);
    }

}
