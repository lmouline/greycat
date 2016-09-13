package org.mwg.core.task;

import org.mwg.plugin.AbstractTaskAction;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskFunctionMap;
import org.mwg.task.TaskResult;

class ActionMap extends AbstractTaskAction {

    private final TaskFunctionMap _map;

    ActionMap(final TaskFunctionMap p_map) {
        super();
        this._map = p_map;
    }

    @Override
    public final void eval(final TaskContext context) {
        final TaskResult previous = context.result();
        final TaskResult next = context.wrap(null);
        final int previousSize = previous.size();
        for (int i = 0; i < previousSize; i++) {
            next.add(_map.map(previous.get(i)));
        }
        context.continueWith(next);
    }

    @Override
    public String toString() {
        return "map()";
    }

}
