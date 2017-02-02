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
import org.mwg.Node;
import org.mwg.task.ActionFunction;
import org.mwg.task.TaskContext;

import static org.mwg.internal.task.CoreActions.inject;
import static org.mwg.task.Tasks.newTask;

public class ActionInjectTest extends AbstractActionTest {

    @Test
    public void test() {
        initGraph();
        newTask()
                .then(inject("uselessPayload"))
                .thenDo(new ActionFunction() {
                    @Override
                    public void eval(TaskContext ctx) {
                        Assert.assertEquals(ctx.resultAsStrings().get(0), "uselessPayload");
                    }
                })
                .execute(graph, null);
        removeGraph();
    }

    @Test
    public void testFromNodes() {
        initGraph();
        final ActionInjectTest selfPointer = this;
        graph.index(0, 0, "nodes", nodes -> {
            graph.lookupAll(0, 0, nodes.all(), result -> {
                Assert.assertEquals(3, result.length);

                String[] expected = new String[]{(String) result[0].get("name"),
                        (String) result[1].get("name"),
                        (String) result[2].get("name")};

                newTask()
                        .then(inject(result))
                        .thenDo(new ActionFunction() {
                            @Override
                            public void eval(TaskContext ctx) {
                                //empty task
                            }
                        })
                        .execute(selfPointer.graph, null);

                String[] resultName = new String[3];
                try {
                    int i = 0;
                    for (Node n : result) {
                        resultName[i] = (String) n.get("name");
                        i++;
                    }
                } catch (Exception e) {
                    resultName[0] = "fail";
                    e.printStackTrace();
                }

                Assert.assertArrayEquals(expected, resultName);
            });
        });

        removeGraph();
    }

    @Test
    public void testFromNode() {
        initGraph();
        final ActionInjectTest selfPointer = this;
        graph.index(0, 0, "roots", rootsIndex -> {
            rootsIndex.find(result -> {
                Assert.assertEquals(1, result.length);

                newTask()
                        .then(inject(result[0]))
                        .thenDo(new ActionFunction() {
                            @Override
                            public void eval(TaskContext ctx) {
                                //empty task
                            }
                        })
                        .execute(graph, null);
                String name;
                try {
                    name = (String) result[0].get("name");
                } catch (Exception e) {
                    name = "fail";
                }

                Assert.assertEquals("root", name);
            },"name","root");
        });

        removeGraph();
    }


}
