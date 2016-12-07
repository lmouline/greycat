package org.mwg.core.task;

import org.junit.Assert;
import org.junit.Test;
import org.mwg.*;
import org.mwg.task.TaskResult;

import static org.mwg.core.task.Actions.timepoints;

public class ActionTimepointsTest {

    @Test
    public void testTP() {
        Graph graph = new GraphBuilder().build();
        graph.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean result) {
                Actions.newTask()
                        .travelInTime("0")
                        .travelInWorld("0")
                        .createNode()
                        .setAsVar("node")
                        .setAttribute("value", Type.INT,"1")
                        .readVar("node")
                        .travelInTime("1")
                        .setAttribute("value", Type.INT,"2")
                        .readVar("node")
                        .travelInTime("3")
                        .setAttribute("value", Type.INT,"3")
                        .readVar("node")
                        .then(timepoints(Constants.BEGINNING_OF_TIME + "", Constants.END_OF_TIME + ""))
                        .execute(graph, new Callback<TaskResult>() {
                            @Override
                            public void on(TaskResult result) {
                                Assert.assertEquals(3,result.size());
                                Assert.assertArrayEquals(new Object[]{3L,1L,0L},result.asArray());
                            }
                        });
            }

        });
    }
}
