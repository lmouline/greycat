package org.mwg.internal.task;

import org.junit.Assert;
import org.junit.Test;
import org.mwg.task.ActionFunction;
import org.mwg.task.TaskContext;

import static org.mwg.internal.task.CoreActions.inject;
import static org.mwg.task.Tasks.newTask;
import static org.mwg.internal.task.CoreActions.readGlobalIndex;

public class ActionFromIndexAllTest extends AbstractActionTest {

    @Test
    public void test() {
        initGraph();
        newTask()
                .then(inject("uselessPayload"))
                .then(readGlobalIndex("nodes"))
                .thenDo(new ActionFunction() {
                    @Override
                    public void eval(TaskContext ctx) {
                        Assert.assertEquals(ctx.resultAsNodes().get(0).get("name"), "n0");
                        Assert.assertEquals(ctx.resultAsNodes().get(1).get("name"), "n1");
                        Assert.assertEquals(ctx.resultAsNodes().get(2).get("name"), "root");
                    }
                }).execute(graph, null);
        removeGraph();
    }

}
