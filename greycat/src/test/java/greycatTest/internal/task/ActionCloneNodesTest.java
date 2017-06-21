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
import greycat.struct.Relation;
import org.junit.Assert;
import org.junit.Test;

import static greycat.Tasks.newTask;

public class ActionCloneNodesTest extends AbstractActionTest {

    public ActionCloneNodesTest() {
        super();
        initGraph();
    }

    @Test
    public void testWithOneNode() {

        newTask()
                .readIndex("nodes")
                .setAsVar("baseNodes")
                .thenDo(ctx -> {
                    ctx.defineVariable("spaceSize", ctx.graph().space().available());
                    ctx.continueTask();
                })
                .log("{{spaceSize}}")
                .cloneNodes()
                .thenDo(new ActionFunction() {
                    @Override
                    public void eval(TaskContext ctx) {
                        //check clone result Size
                        TaskResult<Node> initialNodes = ctx.variable("baseNodes");
                        TaskResult<Node> clones = ctx.resultAsNodes();
                        Assert.assertEquals(initialNodes.size(), clones.size());
                        //check != ids
                        for (int i = 0; i < initialNodes.size(); i++) {
                            Assert.assertNotEquals(initialNodes.get(i).id(), clones.get(i).id());
                        }
                        //check == 'name'
                        for (int i = 0; i < initialNodes.size(); i++) {
                            Assert.assertEquals(initialNodes.get(i).get("name"), clones.get(i).get("name"));
                        }
                        //check == 'children'
                        for (int i = 0; i < initialNodes.size(); i++) {
                            Relation children = initialNodes.get(i).getRelation("children");
                            if (children != null) {
                                Assert.assertArrayEquals(children.all(), clones.get(i).getRelation("children").all());
                            }
                        }
                        ctx.continueTask();
                    }
                })
                .save()
                .clearResult()
                .thenDo(ctx -> {
                    //Check all cloned
                    //Assert no leak
                    Assert.assertEquals(ctx.longVar("spaceSize"), ctx.graph().space().available());
                    ctx.continueTask();
                })
                .execute(graph, new Callback<TaskResult>() {
                    @Override
                    public void on(TaskResult result) {
                        result.free();
                    }
                });
    }


}
