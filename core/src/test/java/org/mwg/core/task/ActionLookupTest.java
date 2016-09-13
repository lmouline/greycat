package org.mwg.core.task;

import org.junit.Assert;
import org.junit.Test;
import org.mwg.Node;
import org.mwg.task.Action;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;

import static org.mwg.task.Actions.newTask;

public class ActionLookupTest extends AbstractActionTest {

    @Test
    public void testLookup() {
        initGraph();

        newTask().fromIndexAll("nodes")
                .then(new Action() {
                    @Override
                    public void eval(TaskContext context) {
                        TaskResult<Node> nodes = context.resultAsNodes();
                        for (int i = 0; i < nodes.size(); i++) {
                            context.addToVariable("ids", nodes.get(i).id());
                        }
                        context.continueTask();
                    }
                })
                .setTime("10")
                .lookupAll("{{ids}}")
                .then(new Action() {
                    @Override
                    public void eval(TaskContext context) {
                        TaskResult<Node> nodes = context.resultAsNodes();
                        Assert.assertEquals(nodes.get(0).toString(),"{\"world\":0,\"time\":10,\"id\":1,\"name\":\"n0\",\"value\":8}");
                        Assert.assertEquals(nodes.get(1).toString(),"{\"world\":0,\"time\":10,\"id\":2,\"name\":\"n1\",\"value\":3}");
                        Assert.assertEquals(nodes.get(2).toString(),"{\"world\":0,\"time\":10,\"id\":3,\"name\":\"root\",\"children\":[1,2]}");

                    }
                })

                .print("{{result}}")
                .execute(graph, null);

        removeGraph();
    }
}
