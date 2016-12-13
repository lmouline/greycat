package org.mwg.core.task;

import org.junit.Assert;
import org.junit.Test;
import org.mwg.task.Task;

import static org.mwg.core.task.Actions.newTask;

public class ParseTest extends AbstractActionTest {

    @Test
    public void test() {
        initGraph();
        Task t = newTask();
        t.toString();
        String toParse = "travelInTime(0).travelInWorld(0).createNode().addToVar(root).createNode().addToVar(root).createNode().addToVar(root).readVar(root).println('{{result}} avec echap \\'')";
        t.parse(toParse, graph);
        Assert.assertEquals("travelInTime(0).travelInTime(0).createNode().addToVar('root').createNode().addToVar('root').createNode().addToVar('root').readVar('root').print('{{result}} avec echap \\'')",t.toString());
        // t.execute(graph, null);
        removeGraph();
    }

    @Test
    public void testSubTask() {
        initGraph();
        Task t = newTask();
        t.parse("travelInTime(0).travelInWorld(0).readGlobalIndex(nodes).loop(0,3,{ print('->{{i}}') })", graph);
        t.execute(graph, null);
        removeGraph();
    }


}
