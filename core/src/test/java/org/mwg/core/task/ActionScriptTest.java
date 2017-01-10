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
                .asyncScript("ctx.setVariable(\"val1\",55).continueTask();")
                .asyncScript("ctx.setVariable(\"val2\",56).continueTask();")
                .asyncScript("ctx.setVariable(\"val4\",70).setVariable(\"val8\",999).continueTask();")
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

    @Test
    public void testPrintScript() {
        initGraph();
        newTask()
                .script("'hello'")
                .defineAsGlobalVar("myVar")
                .println("{{result}}")
                .loop("0", "10", newTask().script("print(myVar.get(0))"))
                .execute(graph, new Callback<TaskResult>() {
                    @Override
                    public void on(TaskResult result) {

                        System.out.println(result.output());
                        if (result.exception() != null) {
                            //result.exception().printStackTrace();
                        }
                        //TODO
                        removeGraph();
                    }
                });
    }

}
