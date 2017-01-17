package org.mwg.task;

import org.mwg.Graph;
import org.mwg.base.BaseTaskResult;
import org.mwg.internal.task.CoreTask;
import org.mwg.internal.task.math.MathConditional;

public class Tasks {

    /**
     * Evaluates a conditional math expression.
     *
     * @param mathExpression the math expression
     * @return the conditional function
     */
    public static ConditionalFunction cond(String mathExpression) {
        return new MathConditional(mathExpression).conditional();
    }

    /**
     * Creates a new task.
     *
     * @return the created task
     */
    public static Task newTask() {
        return new CoreTask();
    }

    /**
     * Creates a new, empty task result.
     *
     * @return an empty task result
     */
    public static TaskResult emptyResult() {
        return new BaseTaskResult(null, false);
    }

    public static Task then(Action action) {
        return newTask().then(action);
    }

    public static Task thenDo(ActionFunction actionFunction) {
        return newTask().thenDo(actionFunction);
    }

    public static Task loop(String from, String to, Task subTask) {
        return newTask().loop(from, to, subTask);
    }

    public static Task loopPar(String from, String to, Task subTask) {
        return newTask().loopPar(from, to, subTask);
    }

    public static Task forEach(Task subTask) {
        return newTask().forEach(subTask);
    }

    public static Task forEachPar(Task subTask) {
        return newTask().forEachPar(subTask);
    }

    public static Task map(Task subTask) {
        return newTask().map(subTask);
    }

    public static Task mapPar(Task subTask) {
        return newTask().mapPar(subTask);
    }

    public static Task ifThen(ConditionalFunction cond, Task then) {
        return newTask().ifThen(cond, then);
    }

    public static Task ifThenScript(String condScript, Task then) {
        return newTask().ifThenScript(condScript, then);
    }

    public static Task ifThenElse(ConditionalFunction cond, Task thenSub, Task elseSub) {
        return newTask().ifThenElse(cond, thenSub, elseSub);
    }

    public static Task ifThenElseScript(String condScript, Task thenSub, Task elseSub) {
        return newTask().ifThenElseScript(condScript, thenSub, elseSub);
    }

    public static Task doWhile(Task task, ConditionalFunction cond) {
        return newTask().doWhile(task, cond);
    }

    public static Task doWhileScript(Task task, String condScript) {
        return newTask().doWhileScript(task, condScript);
    }

    public static Task whileDo(ConditionalFunction cond, Task task) {
        return newTask().whileDo(cond, task);
    }

    public static Task whileDoScript(String condScript, Task task) {
        return newTask().whileDoScript(condScript, task);
    }

    public static Task pipe(Task... subTasks) {
        return newTask().pipe(subTasks);
    }

    public static Task pipePar(Task... subTasks) {
        return newTask().pipePar(subTasks);
    }

    public static Task isolate(Task subTask) {
        return newTask().isolate(subTask);
    }

    public static Task atomic(Task protectedTask, String... variablesToLock) {
        return newTask().atomic(protectedTask, variablesToLock);
    }

    public static Task parse(String flat, Graph graph) {
        return newTask().parse(flat, graph);
    }

}
