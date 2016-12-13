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
        Assert.assertEquals("travelInTime(0).travelInTime(0).createNode().addToVar('root').createNode().addToVar('root').createNode().addToVar('root').readVar('root').println('{{result}} avec echap \\'')", t.toString());
        // t.execute(graph, null);
        removeGraph();
    }

    @Test
    public void testSubTask() {
        initGraph();
        Task t = newTask().parse("travelInTime(0).travelInWorld(0).readGlobalIndex(nodes).loop(0,3,{ println('->{{i}}') })", graph);
        Assert.assertEquals("travelInTime(0).travelInTime(0).readGlobalIndex('nodes',).loop('0','3',println('->{{i}}'))", t.toString());

        //t.execute(graph, null);

        Task t2 = newTask().parse("travelInTime(0).travelInWorld(0).readGlobalIndex(nodes).loopPar(0,3,{ println('->{{i}}') })", graph);
        Assert.assertEquals("travelInTime(0).travelInTime(0).readGlobalIndex('nodes',).loopPar('0','3',println('->{{i}}'))", t2.toString());

        Task t3 = newTask().parse("travelInTime(0).travelInWorld(0).readGlobalIndex(nodes).ifThen('ctx.result().size() > 0', { println('{{result[0]}}') })", graph);
        //t3.execute(graph,null);
        Assert.assertEquals("travelInTime(0).travelInTime(0).readGlobalIndex('nodes',).ifThen('ctx.result().size() > 0',println('{{result[0]}}'))", t3.toString());

        removeGraph();
    }

    @Test
    public void testDAG() {
        initGraph();
        Task mainTask = newTask();
        Task deepR = newTask().println("{{result}}");
        mainTask.ifThenElseScript("ctx.result.size() == 0", deepR, deepR);
        Assert.assertEquals("ifThenElse('ctx.result.size() == 0',0,0)#0{println('{{result}}')}", mainTask.toString());

        Task parsed = newTask().parse("ifThenElse('ctx.result.size() == 0',0,0)#0{println('{{result}}')}", graph);
        Assert.assertEquals("ifThenElse('ctx.result.size() == 0',0,0)#0{println('{{result}}')}", parsed.toString());

        removeGraph();
    }

}
