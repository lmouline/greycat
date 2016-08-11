package org.mwg.core;

import org.mwg.Callback;
import org.mwg.Graph;
import org.mwg.GraphBuilder;
import org.mwg.Type;
import org.mwg.core.scheduler.ExecutorScheduler;
import org.mwg.core.scheduler.HybridScheduler;
import org.mwg.core.scheduler.TrampolineScheduler;
import org.mwg.task.Action;
import org.mwg.task.Task;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;

import java.util.Random;

import static org.mwg.task.Actions.*;

public class BenchmarkParTest {

    public static void main(String[] args) {
        Graph g = new GraphBuilder()
                .withMemorySize(100)
                .saveEvery(50)
                //.withScheduler(new HybridScheduler())
                .withScheduler(new TrampolineScheduler())
                //.withScheduler(new ExecutorScheduler())
                .build();
        g.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean result) {

                final long previous = System.currentTimeMillis();
                final long previousCache = g.space().available();

                repeat("10000", newNode()
                        .setProperty("name", Type.STRING, "node_{{it}}")
                        .indexNode("nodes", "name")
                        .repeat("1000", jump("{{it}}").setProperty("val", Type.INT, "{{it}}").clear())
                        .clear()
                ).save().execute(g, new Callback<TaskResult>() {
                    @Override
                    public void on(TaskResult result) {
                        result.free();
                        long after = System.currentTimeMillis();
                        long afterCache = g.space().available();
                        System.out.println(after - previous + "ms");
                        System.out.println(previousCache + "-" + afterCache);
                    }
                });
            }
        });
        /*
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
    }


}
