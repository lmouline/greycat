package org.mwg.core.task;

import org.junit.Assert;
import org.junit.Test;
import org.mwg.Callback;
import org.mwg.task.ActionFunction;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;

import static org.mwg.core.task.Actions.*;

public class ActionScriptTest extends AbstractActionTest {

    @Test
    public void testSimpleScript() {
        initGraph();
        newTask()
                .then(readGlobalIndex("nodes"))
                .script("ctx.setVariable(\"val1\",55).continueTask();")
                .script("ctx.setVariable(\"val2\",56).continueTask();")
                .script("ctx.setVariable(\"val4\",70).setVariable(\"val8\",999).continueTask();")
                .thenDo(new ActionFunction() {
                    @Override
                    public void eval(TaskContext ctx) {
                        Assert.assertEquals(55, ctx.variable("val1").get(0));
                        Assert.assertEquals(56, ctx.variable("val2").get(0));
                        Assert.assertEquals(70, ctx.variable("val4").get(0));
                        Assert.assertEquals(999, ctx.variable("val8").get(0));
                        ctx.continueWith(ctx.wrap(true));
                    }
                })
                .execute(graph, new Callback<TaskResult>() {
                    @Override
                    public void on(TaskResult result) {
                        Assert.assertEquals(true, result.get(0));
                        removeGraph();
                    }
                });
    }
}
