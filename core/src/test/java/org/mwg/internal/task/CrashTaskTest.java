package org.mwg.internal.task;

import org.junit.Assert;
import org.junit.Test;
import org.mwg.Callback;
import org.mwg.task.ActionFunction;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;

import static org.mwg.internal.task.CoreActions.*;
import static org.mwg.task.Tasks.newTask;

public class CrashTaskTest extends AbstractActionTest {

    @Test
    public void test() {
        initGraph();
        final boolean[] passed = {false};
        newTask()
                .then(inject("input"))
                .thenDo(new ActionFunction() {
                    @Override
                    public void eval(TaskContext ctx) {
                        //produce an NPE
                        String a = null;
                        a.toString();
                    }
                })
                .execute(graph, new Callback<TaskResult>() {
                    @Override
                    public void on(TaskResult result) {
                        passed[0] = true;
                        Assert.assertNotNull(result.exception());
                    }
                });
        Assert.assertTrue(passed[0]);
        removeGraph();
    }

}
