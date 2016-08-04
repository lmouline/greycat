package org.mwg.plugin;

import org.mwg.task.TaskActionFactory;

public interface Plugin {

    Plugin declareNodeType(String name, NodeFactory factory);

    Plugin declareTaskAction(String name, TaskActionFactory factory);

    Plugin declareMemoryFactory(MemoryFactory factory);

    Plugin declareResolverFactory(ResolverFactory factory);

    String[] nodeTypes();

    NodeFactory nodeType(String nodeTypeName);

    String[] taskActionTypes();

    TaskActionFactory taskActionType(String taskTypeName);

    MemoryFactory memoryFactory();

    ResolverFactory resolverFactory();

}
