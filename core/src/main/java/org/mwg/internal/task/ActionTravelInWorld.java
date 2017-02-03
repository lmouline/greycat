package org.mwg.internal.task;

import org.mwg.Callback;
import org.mwg.Constants;
import org.mwg.DeferCounter;
import org.mwg.Node;
import org.mwg.base.BaseNode;
import org.mwg.internal.utility.CoreDeferCounter;
import org.mwg.plugin.Job;
import org.mwg.task.Action;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;


class ActionTravelInWorld implements Action {

    private final String _world;

    ActionTravelInWorld(final String world) {
        _world = world;
    }

    @Override
    public void eval(final TaskContext ctx) {
        final String flatWorld = ctx.template(_world);
        long parsedWorld;
        try {
            parsedWorld = Long.parseLong(flatWorld);
        } catch (Throwable t) {
            Double d = Double.parseDouble(flatWorld);
            parsedWorld = d.longValue();
        }
        ctx.setWorld(parsedWorld);
        final TaskResult previous = ctx.result();
        final DeferCounter defer = new CoreDeferCounter(previous.size());
        final int previousSize = previous.size();
        for (int i = 0; i < previousSize; i++) {
            Object loopObj = previous.get(i);
            if (loopObj instanceof BaseNode) {
                Node castedPreviousNode = (Node) loopObj;
                final int finalIndex = i;
                ctx.graph().lookup(parsedWorld, castedPreviousNode.time(), castedPreviousNode.id(), new Callback<Node>() {
                    @Override
                    public void on(Node result) {
                        castedPreviousNode.free();
                        previous.set(finalIndex, result);
                        defer.count();
                    }
                });
            } else {
                defer.count();
            }
        }
        defer.then(new Job() {
            @Override
            public void run() {
                ctx.continueTask();
            }
        });
    }

    @Override
    public void serialize(StringBuilder builder) {
        builder.append(CoreActionNames.TRAVEL_IN_TIME);
        builder.append(Constants.TASK_PARAM_OPEN);
        builder.append(_world);
        builder.append(Constants.TASK_PARAM_CLOSE);
    }

    @Override
    public String toString() {
        final StringBuilder res = new StringBuilder();
        serialize(res);
        return res.toString();
    }

}
