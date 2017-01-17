package org.mwg.internal.task;

import org.junit.Assert;
import org.junit.Test;
import org.mwg.task.ActionFunction;
import org.mwg.task.TaskContext;

import static org.mwg.internal.task.CoreActions.*;

public class ActionSelectScriptTest extends AbstractActionTest {

    @Test
    public void test() {
        initGraph();
        newTask()
                .readGlobalIndex("nodes")
              //  .select((node, context) -> node.get("name").equals("root"))
                .selectScript("node.get('name') == 'root'")
                .thenDo(new ActionFunction() {
                    @Override
                    public void eval(TaskContext ctx) {
                        Assert.assertEquals(1, ctx.result().size());
                        Assert.assertEquals(ctx.resultAsNodes().get(0).get("name"), "root");
                    }
                })
                .execute(graph, null);
        removeGraph();
    }

    @Test
    public void test2() {
        initGraph();
        newTask()
                .then(readGlobalIndex("nodes"))
                .then(selectScript("false"))
                .thenDo(new ActionFunction() {
                    @Override
                    public void eval(TaskContext ctx) {
                        Assert.assertEquals(ctx.result().size(), 0);
                    }
                })
                .execute(graph, null);
        removeGraph();
    }

    @Test
    public void test3() {
        initGraph();
        newTask()
                .then(readGlobalIndex("nodes"))
                .then(selectScript("true"))
                .thenDo(new ActionFunction() {
                    @Override
                    public void eval(TaskContext ctx) {
                        Assert.assertEquals(ctx.result().size(), 3);
                    }
                })
                .execute(graph, null);
        removeGraph();
    }

}
