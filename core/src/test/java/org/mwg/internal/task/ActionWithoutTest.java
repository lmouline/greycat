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
import org.mwg.task.ActionFunction;
import org.mwg.task.TaskContext;

import static org.mwg.internal.task.CoreActions.readGlobalIndex;
import static org.mwg.internal.task.CoreActions.selectWithout;
import static org.mwg.task.Tasks.newTask;

public class ActionWithoutTest extends AbstractActionTest {

    @Test
    public void test() {
        initGraph();
        newTask()
                .then(readGlobalIndex("nodes"))
                .then(selectWithout("name", "n0"))
                .thenDo(new ActionFunction() {
                    @Override
                    public void eval(TaskContext ctx) {
                        Assert.assertEquals(ctx.resultAsNodes().get(0).get("name"), "n1");
                        Assert.assertEquals(ctx.resultAsNodes().get(1).get("name"), "root");
                    }
                })
                .execute(graph, null);

        newTask().then(readGlobalIndex("nodes"))
                .then(selectWithout("name", "n.*"))
                .thenDo(new ActionFunction() {
                    @Override
                    public void eval(TaskContext ctx) {
                        Assert.assertEquals(ctx.resultAsNodes().get(0).get("name"), "root");
                    }
                })
                .execute(graph, null);
        removeGraph();

    }

}
