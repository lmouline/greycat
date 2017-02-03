package org.mwg.task;

public interface Action {

    void eval(TaskContext ctx);

    void serialize(StringBuilder builder);

}
