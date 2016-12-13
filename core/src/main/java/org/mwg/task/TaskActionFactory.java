package org.mwg.task;

import java.util.Map;

@FunctionalInterface
public interface TaskActionFactory {

    Action create(String[] params, Map<Integer, Task> contextTasks);

}
