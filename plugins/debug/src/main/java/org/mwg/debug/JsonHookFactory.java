package org.mwg.debug;

import org.mwg.task.TaskHook;
import org.mwg.task.TaskHookFactory;

/**
 * Created by lucasm on 19/08/16.
 */
public class JsonHookFactory implements TaskHookFactory {
    @Override
    public TaskHook newHook() {
        return new JsonHook();
    }
}
