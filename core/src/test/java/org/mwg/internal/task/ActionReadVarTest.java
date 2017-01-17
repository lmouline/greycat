package org.mwg.internal.task;

import org.junit.Assert;
import org.junit.Test;
import org.mwg.task.ActionFunction;
import org.mwg.task.TaskContext;

import static org.mwg.internal.task.CoreActions.*;
import static org.mwg.internal.task.CoreActions.newTask;

public class ActionReadVarTest extends AbstractActionTest {

    @Test
    public void test() {
        initGraph();
        newTask()
                .then(readGlobalIndex("nodes"))
                .then(defineAsGlobalVar("x"))
                .then(inject("uselessPayload"))
                .then(readVar("x"))
                .thenDo(new ActionFunction() {
                    @Override
                    public void eval(TaskContext ctx) {
                        Assert.assertEquals(ctx.resultAsNodes().get(0).get("name"), "n0");
                        Assert.assertEquals(ctx.resultAsNodes().get(1).get("name"), "n1");
                        Assert.assertEquals(ctx.resultAsNodes().get(2).get("name"), "root");
                    }
                })
                .execute(graph, null);
        removeGraph();
    }

    @Test
    public void testIndex() {
        initGraph();
        newTask()
                .then(readGlobalIndex("nodes"))
                .then(defineAsGlobalVar("x"))
                .then(inject("uselessPayload"))
                .then(readVar("x[0]"))
                .thenDo(new ActionFunction() {
                    @Override
                    public void eval(TaskContext ctx) {
                        Assert.assertEquals(ctx.resultAsNodes().get(0).get("name"), "n0");
                        Assert.assertEquals(1, ctx.resultAsNodes().size());
                    }
                })
                .execute(graph, null);
        removeGraph();
    }


}
