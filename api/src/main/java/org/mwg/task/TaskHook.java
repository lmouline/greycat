package org.mwg.task;

public interface TaskHook {

    void start(TaskContext initialContext);

    void beforeAction(TaskAction action, TaskContext context);

    void afterAction(TaskAction action, TaskContext context);

    void beforeSubTask(TaskAction action, TaskContext context);

    void afterSubTask(TaskAction action, TaskContext context);

    void end(TaskContext finalContext);

}
