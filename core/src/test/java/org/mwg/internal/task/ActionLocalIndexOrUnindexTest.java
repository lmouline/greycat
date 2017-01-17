package org.mwg.internal.task;

import org.junit.Assert;
import org.junit.Test;
import org.mwg.Callback;
import org.mwg.Graph;
import org.mwg.GraphBuilder;
import org.mwg.Type;
import org.mwg.base.BaseNode;
import org.mwg.task.ActionFunction;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;

import static org.mwg.internal.task.CoreActions.*;
import static org.mwg.internal.task.CoreActions.newTask;

public class ActionLocalIndexOrUnindexTest {

    @Test
    public void testLocalIndex() {
        Graph graph = new GraphBuilder().build();

        graph.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean succeed) {
                newTask()
                        .then(createNode())
                        .then(setAttribute("name", Type.STRING, "child1"))
                        .then(addToVar("child"))
                        .then(createNode())
                        .then(setAttribute("name", Type.STRING, "child2"))
                        .then(addToVar("child"))
                        .then(createNode())
                        .then(setAttribute("name", Type.STRING, "child3"))
                        .then(addToVar("child"))
                        .then(createNode())
                        .then(setAttribute("name", Type.STRING, "root"))
                        .then(addToGlobalIndex("rootIdx", "name"))
                        .then(addVarToRelation("idxRelation", "child", "name"))
                        .then(readGlobalIndex("rootIdx"))
                        .then(traverse("idxRelation"))
                        .thenDo(new ActionFunction() {
                            @Override
                            public void eval(TaskContext ctx) {
                                TaskResult result = ctx.result();
                                Assert.assertEquals(3, result.size());

                                Assert.assertEquals("child1", ((BaseNode) result.get(0)).get("name"));
                                Assert.assertEquals("child2", ((BaseNode) result.get(1)).get("name"));
                                Assert.assertEquals("child3", ((BaseNode) result.get(2)).get("name"));
                            }
                        })
                        .then(readGlobalIndex("rootIdx"))
                        .then(removeVarFromRelation("idxRelation", "child", "name"))
                        .then(readGlobalIndex("rootIdx"))
                        .then(traverse("idxRelation"))
                        .thenDo(new ActionFunction() {
                            @Override
                            public void eval(TaskContext ctx) {
                                TaskResult result = ctx.result();
                                Assert.assertEquals(0, result.size());
                            }
                        })
                        .execute(graph, null);


            }
        });
    }
}
