package org.mwg.internal.task;

import org.junit.Assert;
import org.junit.Test;
import org.mwg.Node;
import org.mwg.task.ActionFunction;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskFunctionSelect;
import org.mwg.utility.HashHelper;

import static org.mwg.internal.task.CoreActions.readGlobalIndex;
import static org.mwg.internal.task.CoreActions.select;
import static org.mwg.task.Tasks.newTask;

public class ActionSelectTest extends AbstractActionTest {

    @Test
    public void test() {
        initGraph();
        newTask()
                .then(readGlobalIndex("nodes"))
                .then(select(new TaskFunctionSelect() {
                    @Override
                    public boolean select(Node node, TaskContext context) {
                        return HashHelper.equals(node.get("name").toString(), "root");
                    }
                }))
                .thenDo(new ActionFunction() {
                    @Override
                    public void eval(TaskContext ctx) {
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
                .then(select((node, context) -> false))
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
                .then(select((node, context) -> true))
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
