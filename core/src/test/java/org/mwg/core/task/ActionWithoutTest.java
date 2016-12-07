package org.mwg.core.task;

import org.junit.Assert;
import org.junit.Test;
import org.mwg.task.ActionFunction;
import org.mwg.task.TaskContext;

import static org.mwg.core.task.Actions.readGlobalIndex;
import static org.mwg.core.task.Actions.selectWithout;
import static org.mwg.core.task.Actions.newTask;

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
