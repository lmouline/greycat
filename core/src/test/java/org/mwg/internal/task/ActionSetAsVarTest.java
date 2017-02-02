/**
 * Copyright 2017 The MWG Authors.  All rights reserved.
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
package org.mwg.internal.task;

import org.junit.Assert;
import org.junit.Test;
import org.mwg.Callback;
import org.mwg.task.ActionFunction;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;

import static org.mwg.internal.task.CoreActions.defineAsGlobalVar;
import static org.mwg.internal.task.CoreActions.inject;
import static org.mwg.task.Tasks.newTask;

public class ActionSetAsVarTest extends AbstractActionTest {

    @Test
    public void test() {
        initGraph();
        newTask()
                .then(inject("hello"))
                .then(defineAsGlobalVar("myVar"))
                .thenDo(new ActionFunction() {
                    @Override
                    public void eval(TaskContext ctx) {
                        Assert.assertEquals(ctx.result().get(0), "hello");
                        Assert.assertEquals(ctx.variable("myVar").get(0), "hello");
                        ctx.continueTask();
                    }
                })
                .execute(graph, new Callback<TaskResult>() {
                    @Override
                    public void on(TaskResult result) {
                        Assert.assertNotEquals(result.size(), 0);
                    }
                });
        removeGraph();
    }

}
