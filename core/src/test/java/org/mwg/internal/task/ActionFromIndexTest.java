package org.mwg.internal.task;

import org.junit.Assert;
import org.junit.Test;
import org.mwg.task.ActionFunction;
import org.mwg.task.TaskContext;

import static org.mwg.internal.task.CoreActions.readGlobalIndex;
import static org.mwg.internal.task.CoreActions.inject;
import static org.mwg.task.Tasks.newTask;

public class ActionFromIndexTest extends AbstractActionTest {

    @Test
    public void test() {
        initGraph();
        newTask()
                .then(inject("uselessPayload"))
                .then(readGlobalIndex("nodes", "name", "n0"))
                .thenDo(new ActionFunction() {
                    @Override
                    public void eval(TaskContext ctx) {
                        Assert.assertEquals(ctx.resultAsNodes().get(0).get("name"), "n0");
                        Assert.assertEquals(ctx.result().size(), 1);
                    }
                })
                .execute(graph, null);
        removeGraph();
    }

}
