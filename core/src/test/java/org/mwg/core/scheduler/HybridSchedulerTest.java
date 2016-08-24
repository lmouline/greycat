package org.mwg.core.scheduler;

import org.mwg.Callback;
import org.mwg.Graph;
import org.mwg.GraphBuilder;
import org.mwg.task.TaskResult;

import static org.mwg.task.Actions.print;
import static org.mwg.task.Actions.loop;
import static org.mwg.task.Actions.loopPar;

/**
 * @ignore ts
 */
public class HybridSchedulerTest {

  //  @Test
    public void test() {
        Graph g = new GraphBuilder().withScheduler(new HybridScheduler()).build();
        g.connect(new Callback<Boolean>() {
            @Override
            public void on(Boolean result) {
                loopPar("0","99", loop("0","99",print("{{result}}"))).execute(g, new Callback<TaskResult>() {
                    @Override
                    public void on(TaskResult result) {
                        System.out.println();

                    }
                });
            }
        });

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
