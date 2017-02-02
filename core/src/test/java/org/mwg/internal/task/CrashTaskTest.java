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

import org.junit.Assert;
import org.junit.Test;
import org.mwg.Callback;
import org.mwg.task.ActionFunction;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;

import static org.mwg.internal.task.CoreActions.*;
import static org.mwg.task.Tasks.newTask;

public class CrashTaskTest extends AbstractActionTest {

    @Test
    public void test() {
        initGraph();
        final boolean[] passed = {false};
        newTask()
                .then(inject("input"))
                .thenDo(new ActionFunction() {
                    @Override
                    public void eval(TaskContext ctx) {
                        //produce an NPE
                        String a = null;
                        a.toString();
                    }
                })
                .execute(graph, new Callback<TaskResult>() {
                    @Override
                    public void on(TaskResult result) {
                        passed[0] = true;
                        Assert.assertNotNull(result.exception());
                    }
                });
        Assert.assertTrue(passed[0]);
        removeGraph();
    }

}
