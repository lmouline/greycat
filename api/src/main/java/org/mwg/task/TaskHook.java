package org.mwg.task;

public interface TaskHook {

    void start(TaskContext initialContext);

    void beforeAction(TaskAction action, TaskContext context);

    void afterAction(TaskAction action, TaskContext context);

    void beforeTask(TaskContext parentContext, TaskContext context);

    void afterTask(TaskContext context);

    void end(TaskContext finalContext);

}
