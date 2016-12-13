package org.mwg.task;

import java.util.Map;

public interface Action {

    void eval(TaskContext ctx);

    void serialize(StringBuilder builder);

}
