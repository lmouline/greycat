package org.mwg.plugin;

import org.mwg.task.Action;

@FunctionalInterface
public interface ActionFactory {

    Action create(Object[] params);

}
