package org.mwg.core.task;


import org.junit.Assert;
import org.junit.Test;
import org.mwg.*;
import org.mwg.task.Action;
import org.mwg.task.Actions;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;

public class ActionPropertiesNamesTest {
    private Graph graph;

    public void initGraph() {
        graph = new GraphBuilder().build();
        graph.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean result) {
                Node root = graph.newNode(0,0);
                root.setProperty("id", Type.INT,1);
                root.setProperty("attribute",Type.BOOL,false);
                graph.index("root",root,"id",null);

                Node child1 = graph.newNode(0,0);
                child1.set("name","child1");
                root.add("rel1",child1);

                root.index("localIindex1",child1,"name",null);
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
                .properties()
                .then(new Action() {
                    @Override
                    public void eval(TaskContext context) {
                        TaskResult<String> result = context.result();
                        Assert.assertEquals(4,result.size());

                        Assert.assertEquals("id",result.get(0));
                        Assert.assertEquals("attribute",result.get(1));
                        Assert.assertEquals("rel1",result.get(2));
                        Assert.assertEquals("localIindex1",result.get(3));
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
                .propertiesWithType(Type.RELATION)
                .then(new Action() {
                    @Override
                    public void eval(TaskContext context) {
                        TaskResult<String> result = context.result();
                        Assert.assertEquals(1,result.size());

                        Assert.assertEquals("rel1",result.get(0));
                    }
                })
                .execute(graph,null);
        deleteGraph();
    }
}
