package org.mwg.task;

@FunctionalInterface
public interface ActionFunction {
    void eval(TaskContext ctx);
}
