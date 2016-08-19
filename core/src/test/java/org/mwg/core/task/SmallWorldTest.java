package org.mwg.core.task;

import org.mwg.Graph;
import org.mwg.GraphBuilder;
import org.mwg.Type;
import org.mwg.task.TaskAction;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskHook;
import org.mwg.task.TaskHookFactory;

import static org.mwg.task.Actions.*;

public class SmallWorldTest {

    public static void main(String[] args) {


        TaskHookFactory hf = new TaskHookFactory() {
            @Override
            public TaskHook newHook() {
                return new TaskHook() {
                    @Override
                    public void start(TaskContext initialContext) {
                        System.out.println("Start");
                    }

                    @Override
                    public void beforeAction(TaskAction action, TaskContext context) {
                        System.out.println("Before:" + action);
                    }

                    @Override
                    public void afterAction(TaskAction action, TaskContext context) {
                        System.out.println("After:" + action);
                    }

                    @Override
                    public void beforeSubTask(TaskAction action, TaskContext context) {
                        System.out.println("In:" + action);
                    }

                    @Override
                    public void afterSubTask(TaskAction action, TaskContext context) {
                        System.out.println("Out:" + action);
                    }

                    @Override
                    public void end(TaskContext finalContext) {
                        System.out.println("Out");
                    }
                };
            }
        };

        Graph g = new GraphBuilder()
                .withMemorySize(100000)
                .build();
        g.connect(isConnected -> {

            setTime("0").setWorld("0")
                    .newNode().setProperty("name", Type.STRING, "room0").indexNode("rooms", "name").asVar("room0")
                    .newNode().setProperty("name", Type.STRING, "room01").indexNode("rooms", "name").asVar("room01")
                    .newNode().setProperty("name", Type.STRING, "room001").indexNode("rooms", "name").asVar("room001")
                    .newNode().setProperty("name", Type.STRING, "room0001").indexNode("rooms", "name").asVar("room0001")
                    .fromVar("room0").add("rooms", "room01")
                    .fromVar("room01").add("rooms", "room001")
                    .fromVar("room001").add("rooms", "room0001")
                    .repeat("10", //repeat automatically inject an it variable
                            newNode()
                                    .setProperty("id", Type.STRING, "sensor_{{it}}")
                                    .indexNode("sensors", "id")
                                    .defineVar("sensor")
                                    .ifThenElse(cond("it % 4 == 0"), fromVar("room0").add("sensors", "sensor"),
                                            ifThenElse(cond("it % 4 == 1"), fromVar("room01").add("sensors", "sensor"),
                                                    ifThenElse(cond("it % 4 == 2"), fromVar("room001").add("sensors", "sensor"),
                                                            ifThen(cond("it % 4 == 3"), fromVar("room0001").add("sensors", "sensor")))))
                    )
                    .hook(hf).execute(g, taskResult -> {
                if (taskResult != null) {
                    taskResult.free();
                }
                System.out.println("MWG Server listener through :8050");
            });

        });
    }

}
