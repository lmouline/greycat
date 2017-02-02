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
import org.mwg.Graph;
import org.mwg.GraphBuilder;
import org.mwg.task.ActionFunction;
import org.mwg.task.TaskContext;

import static org.mwg.internal.task.CoreActions.*;
import static org.mwg.task.Tasks.newTask;

public class CoreTaskContextTests {

    @Test
    public void testArrayInTemplate() {
        Graph graph = new GraphBuilder().build();
        graph.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean result) {
                newTask()
                        .then(inject(4))
                        .then(defineAsGlobalVar("i"))
                        .then(inject(new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9}))
                        .then(defineAsGlobalVar("array"))
                        .then(readVar("array"))
                        .thenDo(new ActionFunction() {
                            @Override
                            public void eval(TaskContext ctx) {
                                Assert.assertEquals("5", ctx.template("{{array[4]}}"));
                                Assert.assertEquals("9", ctx.template("{{result[8]}}"));
                                Assert.assertEquals("[1,2,3,4,5,6,7,8,9]", ctx.template("{{result}}"));
                                Assert.assertEquals("[1,2,3,4,5,6,7,8,9]", ctx.template("{{array}}"));

                                /*
                                boolean exceptionCaught = false;
                                try {
                                    context.template("{{result[]}}");
                                } catch (RuntimeException e) {
                                    exceptionCaught = true;
                                }
                                Assert.assertTrue(exceptionCaught);

                                exceptionCaught = false;
                                try {
                                    System.out.println(context.template("{{result[9]}}"));;
                                } catch (RuntimeException e) {
                                    exceptionCaught = true;
                                }
                                Assert.assertTrue(exceptionCaught);
*/
                                Assert.assertEquals("9.1", ctx.template("{{=((1 + 2) + (array[6] - 4) * 2) + 0.1 }}"));
                            }
                        })
                        .execute(graph, null);
            }
        });
    }
}
