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
package greycatTest.internal.task;

import greycat.*;
import org.junit.Assert;
import org.junit.Test;

public class ActionIfThenTask {

    @Test
    public void testNormal() {
        Graph graph = new GraphBuilder().build();
        graph.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean result) {
                final int[] checker = new int[]{0};
                Tasks.newTask()
                        .ifThenTask(Tasks.newTask().inject(true),
                                Tasks.newTask().thenDo(ctx -> {checker[0]++; ctx.continueTask();}))
                        .ifThenTask(Tasks.newTask().inject(false),
                                Tasks.newTask().thenDo(ctx -> {checker[0]++; ctx.continueTask();}))
                        .execute(graph, new Callback<TaskResult>() {
                            @Override
                            public void on(TaskResult result) {
                                Assert.assertEquals(1,checker[0]);
                                Assert.assertNull(result.exception());
                            }
                        });
            }
        });
    }

    @Test
    public void testError() {
        Graph graph = new GraphBuilder().build();
        graph.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean result) {
                Tasks.newTask()
                        .ifThenTask(Tasks.newTask(),
                                Tasks.newTask().thenDo(ctx -> ctx.continueTask()))
                        .execute(graph, new Callback<TaskResult>() {
                            @Override
                            public void on(TaskResult result) {
                                Assert.assertNotNull(result.exception());
                            }
                        });
            }
        });
    }
}
