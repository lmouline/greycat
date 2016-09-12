package org.mwg.core.task;


import org.junit.Assert;
import org.junit.Test;
import org.mwg.*;
import org.mwg.task.Action;
import org.mwg.task.Actions;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;

public class ActionRelationsTest {
    private Graph graph;

    public void initGraph() {
        graph = new GraphBuilder().build();
        graph.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean result) {
                Node root = graph.newNode(0,0);
                root.setProperty("id", Type.INT,1);
                graph.index("root",root,"id",null);

                Node child1 = graph.newNode(0,0);
                child1.set("name","child1");
                root.add("rel1",child1);
                root.add("rel2", child1);
                root.add("rel3", child1);

                root.index("localIindex1",child1,"name",null);
                root.index("localIindex2",child1,"name",null);
                root.index("localIindex3",child1,"name",null);
            }
        });
    }

    public void deleteGraph() {
        graph.disconnect(null);
    }

    @Test
    public void testNormalRelations() {
        initGraph();
        Actions.fromIndexAll("root")
                .relations()
                .then(new Action() {
                    @Override
                    public void eval(TaskContext context) {
                        TaskResult<String> result = context.result();
                        Assert.assertEquals(3,result.size());

                        Assert.assertEquals("rel1",result.get(0));
                        Assert.assertEquals("rel2",result.get(1));
                        Assert.assertEquals("rel3",result.get(2));
                        context.continueTask();
                    }
                })
                .execute(graph,null);
        deleteGraph();
    }

    @Test
    public void testLocalIndex() {
        initGraph();
        Actions.fromIndexAll("root")
                .localIndexes()
                .then(new Action() {
                    @Override
                    public void eval(TaskContext context) {
                        TaskResult<String> result = context.result();
                        Assert.assertEquals(3,result.size());

                        Assert.assertEquals("localIindex1",result.get(0));
                        Assert.assertEquals("localIindex2",result.get(1));
                        Assert.assertEquals("localIindex3",result.get(2));
                    }
                })
                .execute(graph,null);
        deleteGraph();
    }
}
