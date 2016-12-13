package org.mwg.core.task;

import org.mwg.Constants;
import org.mwg.DeferCounter;
import org.mwg.base.BaseNode;
import org.mwg.core.utility.CoreDeferCounter;
import org.mwg.plugin.Job;
import org.mwg.task.Action;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;

class ActionAddToGlobalIndex implements Action {

    private final String _name;
    private final String[] _attributes;

    ActionAddToGlobalIndex(final String name, final String... attributes) {
        this._name = name;
        this._attributes = attributes;
    }

    @Override
    public void eval(final TaskContext ctx) {
        final TaskResult previousResult = ctx.result();
        final String templatedIndexName = ctx.template(_name);
        final String[] templatedAttributes = ctx.templates(_attributes);
        final DeferCounter counter = new CoreDeferCounter(previousResult.size());
        for (int i = 0; i < previousResult.size(); i++) {
            final Object loop = previousResult.get(i);
            if (loop instanceof BaseNode) {
                BaseNode loopBaseNode = (BaseNode) loop;
                ctx.graph().index(loopBaseNode.world(), Constants.BEGINNING_OF_TIME, templatedIndexName, indexNode -> {
                    indexNode.addToIndex(loopBaseNode, templatedAttributes);
                    counter.count();
                });
            } else {
                counter.count();
            }
        }
        counter.then(new Job() {
            @Override
            public void run() {
                ctx.continueTask();
            }
        });
    }

    @Override
    public void serialize(StringBuilder builder) {
        builder.append(ActionNames.ADD_TO_GLOBAL_INDEX);
        builder.append(Constants.TASK_PARAM_OPEN);
        TaskHelper.serializeString(_name, builder);
        builder.append(Constants.TASK_PARAM_SEP);
        TaskHelper.serializeStringParams(_attributes, builder);
        builder.append(Constants.TASK_PARAM_CLOSE);
    }

    @Override
    public String toString() {
        final StringBuilder res = new StringBuilder();
        serialize(res);
        return res.toString();
    }

}
