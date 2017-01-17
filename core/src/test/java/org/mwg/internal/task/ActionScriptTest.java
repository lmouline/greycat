package org.mwg.internal.task;

import org.junit.Assert;
import org.junit.Test;
import org.mwg.Callback;
import org.mwg.task.ActionFunction;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;

import static org.mwg.internal.task.CoreActions.*;

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
                .script("'hello'") //will put result in the result
                .defineAsGlobalVar("myVar")
                .println("{{result}}")
                .loop("0", "2", newTask().script("print(myVar.get(0))"))
                .execute(graph, new Callback<TaskResult>() {
                    @Override
                    public void on(TaskResult result) {
                        Assert.assertNull(result.exception());
                        Assert.assertEquals("hello\nhello\nhello\nhello\n", result.output());
                        removeGraph();
                    }
                });
    }


    @Test
    public void testVarScript() {
        initGraph();
        newTask()
                .script("'time='+new Date(1484123443411).getTime()") //will put result in the result
                .defineAsGlobalVar("myVar")
                .script("print(myVar.get(0))")
                .inject("3")
                .println("{{result}}")
                .execute(graph, new Callback<TaskResult>() {
                    @Override
                    public void on(TaskResult result) {
                        Assert.assertNull(result.exception());
                        Assert.assertEquals("time=1484123443411\n3\n", result.output());
                        removeGraph();
                    }
                });
    }

}
