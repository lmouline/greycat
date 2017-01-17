package org.mwg.internal.task;

import org.mwg.Constants;
import org.mwg.DeferCounter;
import org.mwg.base.BaseNode;
import org.mwg.internal.utility.CoreDeferCounter;
import org.mwg.plugin.Job;
import org.mwg.task.Action;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;

class ActionAddRemoveToGlobalIndex implements Action {

    private final String _name;
    private final String[] _attributes;
    private final boolean _timed;
    private final boolean _remove;

    ActionAddRemoveToGlobalIndex(final boolean remove, final boolean timed, final String name, final String... attributes) {
        this._name = name;
        this._timed = timed;
        this._attributes = attributes;
        this._remove = remove;
    }

    @Override
    public final void eval(final TaskContext ctx) {
        final TaskResult previousResult = ctx.result();
        final String templatedIndexName = ctx.template(_name);
        final String[] templatedAttributes = ctx.templates(_attributes);
        final DeferCounter counter = new CoreDeferCounter(previousResult.size());
        for (int i = 0; i < previousResult.size(); i++) {
            final Object loop = previousResult.get(i);
            if (loop instanceof BaseNode) {
                BaseNode loopBaseNode = (BaseNode) loop;
                long indexTime = Constants.BEGINNING_OF_TIME;
                if (_timed) {
                    indexTime = ctx.time();
                }
                ctx.graph().index(loopBaseNode.world(), indexTime, templatedIndexName, indexNode -> {
                    if(_remove){
                        indexNode.removeFromIndex(loopBaseNode, templatedAttributes);
                    } else {
                        indexNode.addToIndex(loopBaseNode, templatedAttributes);
                    }
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
    public final void serialize(StringBuilder builder) {
        if (_timed) {
            builder.append(CoreActionNames.ADD_TO_GLOBAL_TIMED_INDEX);
        } else {
            builder.append(CoreActionNames.ADD_TO_GLOBAL_INDEX);
        }
        builder.append(Constants.TASK_PARAM_OPEN);
        TaskHelper.serializeString(_name, builder, true);
        builder.append(Constants.TASK_PARAM_SEP);
        TaskHelper.serializeStringParams(_attributes, builder);
        builder.append(Constants.TASK_PARAM_CLOSE);
    }

    @Override
    public final String toString() {
        final StringBuilder res = new StringBuilder();
        serialize(res);
        return res.toString();
    }

}
