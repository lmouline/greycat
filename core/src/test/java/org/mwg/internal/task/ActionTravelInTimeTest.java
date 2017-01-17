package org.mwg.internal.task;

import org.junit.Assert;
import org.junit.Test;
import org.mwg.Constants;
import org.mwg.Node;
import org.mwg.task.ActionFunction;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;

import static org.mwg.internal.task.CoreActions.*;
import static org.mwg.internal.task.CoreActions.newTask;

public class ActionTravelInTimeTest extends AbstractActionTest {

    @Test
    public void testTravelInTime() {
        initGraph();

        newTask().then(readGlobalIndex("nodes"))
                .then(defineAsGlobalVar("nodes"))
                .forEach(newTask().thenDo(new ActionFunction() {
                    @Override
                    public void eval(TaskContext ctx) {
                        TaskResult<Node> nodes = ctx.resultAsNodes();
                        Assert.assertEquals(Constants.BEGINNING_OF_TIME, nodes.get(0).time());
                        ctx.continueWith(null);
                    }
                }))
                .then(readVar("nodes"))
                .then(travelInTime("10"))
                .forEach(newTask().thenDo(new ActionFunction() {
                    @Override
                    public void eval(TaskContext ctx) {
                        TaskResult<Node> nodes = ctx.resultAsNodes();
                        Node it = nodes.get(0);
                        Assert.assertEquals(10, it.time());
                        ctx.continueWith(null);
                    }
                }))
                .execute(graph, null);


        removeGraph();
    }
}
