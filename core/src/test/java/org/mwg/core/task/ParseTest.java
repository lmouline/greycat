package org.mwg.core.task;

import org.junit.Test;

import static org.mwg.core.task.Actions.newTask;

public class ParseTest extends AbstractActionTest {

    @Test
    public void test() {
        initGraph();
        newTask().parse("travelInTime(0).travelInWorld(0).createNode().addToVar(root).createNode().addToVar(root).createNode().addToVar(root).readVar(root).println('{{result}} avec echap \\'')", graph).execute(graph, null);
        removeGraph();
    }

}
