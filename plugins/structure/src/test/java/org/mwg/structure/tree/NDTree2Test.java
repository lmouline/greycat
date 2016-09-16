package org.mwg.structure.tree;

import org.junit.Test;
import org.mwg.*;
import org.mwg.structure.StructurePlugin;
import org.mwg.structure.action.TraverseById;
import org.mwg.structure.distance.Distances;
import org.mwg.task.Action;
import org.mwg.task.Task;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;

import static org.mwg.task.Actions.*;

/**
 * Created by assaad on 16/09/16.
 */
public class NDTree2Test {

    private static void printgraph(NDTree2 root){
        Task printer= newTask();

        printer
                .defineVar("parent")
                .println("{{tabs}}")
                .math("tabs+1")
                .println("{{result}}")
                .asVar("{{tabs}}")
                .println("{{tabs}}");


             //   .loop("0","{{tabs}}",print("\t"))
             //   .println("{{result}}")
             //   .fromVar("parent")
             //   .propertiesWithTypes(Type.RELATION)
             //   .foreach(defineVar("traverseID").fromVar("parent").action(TraverseById.NAME,"{{traverseID}}").foreach(subTask(printer)));

        TaskContext tc=printer.prepareWith(root.graph(), root, new Callback<TaskResult>() {
            @Override
            public void on(TaskResult result) {
                System.out.println("--");
                result.free();
            }
        });

        tc.setGlobalVariable("tabs",0.0);
        printer.executeUsing(tc);


    }


    private void insert(double x, double y, NDTree2 ndTree2){
        double[] temp={x,y};
        Node tempNode = ndTree2.graph().newNode(0,0);
        tempNode.set("key",temp);
        ndTree2.insertWith(temp,tempNode,null);
        printgraph(ndTree2);
    }

    @Test
    public void test1() {
        final Graph graph = new GraphBuilder()
                .withPlugin(new StructurePlugin())
                .withMemorySize(1000)
                //.withOffHeapMemory()
                .build();
        graph.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean result) {
                NDTree2 ndTree2 = (NDTree2) graph.newTypedNode(0, 0, NDTree2.NAME);
                ndTree2.setDistance(Distances.EUCLIDEAN);
                double[] min={0,0};
                double[] max={1,1};

                ndTree2.setBounds(min,max);

                insert(0.1,0.1,ndTree2);
                //insert(0.2,0.2,ndTree2);
                //insert(0.3,0.3,ndTree2);
                //insert(0.4,0.4,ndTree2);
                //insert(0.8,0.6,ndTree2);


            }
        });



    }
}
