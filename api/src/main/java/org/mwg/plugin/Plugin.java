package org.mwg.plugin;

import org.mwg.task.TaskActionFactory;
import org.mwg.task.TaskHookFactory;

public interface Plugin {

    Plugin declareNodeType(String name, NodeFactory factory);

    Plugin declareTaskAction(String name, TaskActionFactory factory);

    Plugin declareMemoryFactory(MemoryFactory factory);

    Plugin declareTaskHookFactory(TaskHookFactory factory);

    Plugin declareResolverFactory(ResolverFactory factory);

    TaskHookFactory hookFactory();

    String[] nodeTypes();

    NodeFactory nodeType(String nodeTypeName);

    String[] taskActionTypes();

    TaskActionFactory taskActionType(String taskTypeName);

    MemoryFactory memoryFactory();

    ResolverFactory resolverFactory();

    void stop();

}
