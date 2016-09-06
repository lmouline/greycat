package org.mwg.memory.offheap;

import org.mwg.Callback;
import org.mwg.Graph;
import org.mwg.GraphBuilder;
import org.mwg.Type;
import org.mwg.task.TaskResult;

import static org.mwg.task.Actions.*;

@SuppressWarnings("Duplicates")
public class ParTaskMicroBenchmark {

    public static void main(String[] args) {
        Graph g = new GraphBuilder()
                .withMemorySize(10000)
                //.withPlugin(new OffHeapMemoryPlugin())
                //.withScheduler(new HybridScheduler())
                .build();
        g.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean result) {
                final long previous = System.currentTimeMillis();
                final long previousCache = g.space().available();
                loopPar("0", "9999", newNode()
                        .setProperty("name", Type.STRING, "node_{{it}}")
                        // .print("{{result}}")
                        .indexNode("nodes", "name")
                        .loop("0", "999", jump("{{it}}").setProperty("val", Type.INT, "{{it}}").clear())
                        .save()
                        .clear()
                ).save().execute(g, new Callback<TaskResult>() {
                    @Override
                    public void on(TaskResult result) {
                        result.free();
                        long after = System.currentTimeMillis();
                        long afterCache = g.space().available();
                        System.out.println(after - previous + "ms");
                        System.out.println(previousCache + "-" + afterCache);
                        g.disconnect(new Callback<Boolean>() {
                            @Override
                            public void on(Boolean result) {

                            }
                        });
                    }
                });
            }
        });
    }


}
