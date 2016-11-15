package org.mwg.memory.offheap;

import org.mwg.Callback;
import org.mwg.Graph;
import org.mwg.GraphBuilder;
import org.mwg.Type;
import org.mwg.core.scheduler.HybridScheduler;
import org.mwg.task.TaskResult;

import static org.mwg.task.Actions.*;

@SuppressWarnings("Duplicates")
public class BenchmarkParTask {

    public static void main(String[] args) {

        Graph g = new GraphBuilder()
                .withMemorySize(1000000)
                .withPlugin(new OffHeapMemoryPlugin())
                .withScheduler(new HybridScheduler())
                .build();
        g.connect(result -> {
            final long previous = System.currentTimeMillis();
            final long previousCache = g.space().available();
            loopPar("0", "9999", newNode()
                    .setProperty("name", Type.STRING, "node_{{i}}")
                    //.print("{{result}}")
                    .indexNode("nodes", "name")
                    .loop("0", "99", jump("{{i}}").setProperty("val", Type.INT, "{{i}}").clear())
                    .ifThen(cond("i % 1 == 0"), save())
                    .clear()
            ).save().fromIndexAll("nodes").execute(g, new Callback<TaskResult>() {
                @Override
                public void on(TaskResult result) {
                    System.out.println("indexSize=" + result.size());
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
        });
    }

}
