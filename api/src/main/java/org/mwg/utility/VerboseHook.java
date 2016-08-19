package org.mwg.utility;

import org.mwg.task.TaskAction;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskHook;
import org.mwg.task.TaskResult;

class VerboseHook implements TaskHook {

    //@Override
    //public void on(TaskAction previous, TaskAction next, TaskContext context) {
        /*
        for (int i = 0; i < context.ident(); i++) {
            System.out.print("\t");
        }
        String taskName = next.toString();
        System.out.println(context.template(taskName));
        */
        /*
        for (int i = 0; i < context.ident(); i++) {
            System.out.print("\t");
            System.out.println(context.result().toString());
        }*/
    //}

    @Override
    public void start(TaskContext initialContext) {

    }

    @Override
    public void beforeAction(TaskAction action, TaskContext context) {

    }

    @Override
    public void afterAction(TaskAction action, TaskContext context) {

    }

    @Override
    public void beforeSubTask(TaskAction action, TaskContext context) {

    }

    @Override
    public void afterSubTask(TaskAction action, TaskContext context) {

    }

    @Override
    public void end(TaskContext initialContext) {

    }
}
