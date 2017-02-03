package org.mwg.plugin;

import org.mwg.Callback;
import org.mwg.task.Task;

public interface TaskExecutor {

    void executeTasks(Callback<String[]> callback, Task... tasks);

}
