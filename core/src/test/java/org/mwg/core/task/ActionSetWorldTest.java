package org.mwg.core.task;

import org.junit.Assert;
import org.junit.Test;
import org.mwg.task.ActionFunction;
import org.mwg.task.TaskContext;

import static org.mwg.core.task.Actions.newTask;
import static org.mwg.core.task.Actions.travelInWorld;

public class ActionSetWorldTest extends AbstractActionTest {

    @Test
    public void test() {
        initGraph();
        newTask()
                .then(travelInWorld("10"))
                .thenDo(new ActionFunction() {
                    @Override
                    public void eval(TaskContext ctx) {
                        Assert.assertEquals(ctx.world(), 10);
                    }
                })
                .execute(graph,null);
        removeGraph();
    }

}
