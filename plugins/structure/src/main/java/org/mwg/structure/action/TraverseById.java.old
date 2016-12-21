package org.mwg.structure.action;

import org.mwg.Callback;
import org.mwg.Constants;
import org.mwg.DeferCounter;
import org.mwg.Node;
import org.mwg.base.BaseNode;
import org.mwg.core.task.TaskHelper;
import org.mwg.plugin.Job;
import org.mwg.task.Action;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;

public class TraverseById implements Action {

    public static String NAME = "traverseById";

    private final String _name;

    public TraverseById(final String p_name) {
        this._name = p_name;
    }

    @Override
    public final void eval(final TaskContext ctx) {
        final TaskResult finalResult = ctx.wrap(null);
        final long flatlongName = Long.parseLong(ctx.template(_name));
        final TaskResult previousResult = ctx.result();
        if (previousResult != null) {
            final int previousSize = previousResult.size();
            final DeferCounter defer = ctx.graph().newCounter(previousSize);
            for (int i = 0; i < previousSize; i++) {
                final Object loop = previousResult.get(i);
                if (loop instanceof BaseNode) {
                    final Node casted = (Node) loop;
                    casted.relationAt(flatlongName, new Callback<Node[]>() {
                        @Override
                        public void on(Node[] result) {
                            if (result != null) {
                                for (int j = 0; j < result.length; j++) {
                                    finalResult.add(result[j]);
                                }
                            }
                            casted.free();
                            defer.count();
                        }
                    });
                } else {
                    finalResult.add(loop);
                    defer.count();
                }
            }
            defer.then(new Job() {
                @Override
                public void run() {
                    //optimization to avoid agin iteration on the previous result set
                    previousResult.clear();
                    ctx.continueWith(finalResult);
                }
            });
        } else {
            ctx.continueTask();
        }
    }

    @Override
    public void serialize(StringBuilder builder) {
        builder.append(NAME);
        builder.append(Constants.TASK_PARAM_OPEN);
        TaskHelper.serializeString(_name, builder);
        builder.append(Constants.TASK_PARAM_CLOSE);
    }

    @Override
    public String toString() {
        final StringBuilder res = new StringBuilder();
        serialize(res);
        return res.toString();
    }

}