/**
 * Copyright 2017 The GreyCat Authors.  All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package greycat.internal.task;

import greycat.*;
import greycat.plugin.SchedulerAffinity;
import greycat.struct.Buffer;

import java.util.Map;

class CF_IfThenTask extends CF_Action {
    private final Task condition;
    private final Task action;

    CF_IfThenTask(Task condTask, Task then) {
        super();
        this.condition = condTask;
        this.action = then;
    }

    @Override
    public Task[] children() {
        Task[] children_tasks = new Task[1];
        children_tasks[0] = action;
        return children_tasks;
    }

    @Override
    public void cf_serialize(Buffer builder, Map<Integer, Integer> dagIDS) {

    }

    @Override
    public void eval(TaskContext ctx) {
        condition.executeFrom(ctx, ctx.result(), SchedulerAffinity.SAME_THREAD, new Callback<TaskResult>() {
            @Override
            public void on(TaskResult result) {
                if(result != null || result.size() == 1) {
                    if(result.exception() != null) {
                        ctx.endTask(result,result.exception());
                    } else {
                        try {
                            if ((Boolean) result.get(0)) {
                                action.executeFrom(ctx, ctx.result(), SchedulerAffinity.SAME_THREAD, new Callback<TaskResult>() {
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
                        } catch (RuntimeException e) {
                            ctx.endTask(result,e);

                        }
                    }
                } else {
                    ctx.endTask(result,new RuntimeException("2 / The condition task should finish with a boolean result."));
                }
            }
        });
    }


    @Override
    public String name() {
        return "IfThenTask";
    }
}
