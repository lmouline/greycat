package org.mwg.internal.task;

import org.junit.Assert;
import org.junit.Test;
import org.mwg.Callback;
import org.mwg.task.TaskResult;

import static org.mwg.internal.task.CoreActions.inject;
import static org.mwg.task.Tasks.newTask;

public class TaskOutputTest extends AbstractActionTest {

    @Test
    public void test() {
        initGraph();
        final boolean[] passed = {false};
        newTask()
                .then(inject("input"))
                .defineAsVar("myVar")
                .print("{{myVar}}")
                .execute(graph, new Callback<TaskResult>() {
                    @Override
                    public void on(TaskResult result) {
                        passed[0] = true;
                        Assert.assertEquals(result.output(), "input");
                        result.free();
                    }
                });
        Assert.assertTrue(passed[0]);
        removeGraph();
    }

    @Test
    public void forEachTest() {
        initGraph();
        final boolean[] passed = {false};
        newTask()
                .print("{")
                .loop("1", "5", newTask().print("{{i}}"))
                .print("}")
                .execute(graph, new Callback<TaskResult>() {
                    @Override
                    public void on(TaskResult result) {
                        passed[0] = true;
                        Assert.assertEquals(result.output(), "{12345}");
                        result.free();
                    }
                });
        Assert.assertTrue(passed[0]);
        removeGraph();
    }

}
