package greycat.ml.actions;

import greycat.*;
import greycat.base.BaseNode;
import greycat.internal.task.TaskHelper;
import greycat.plugin.Job;
import greycat.task.Action;
import greycat.task.TaskContext;
import greycat.task.TaskResult;
import org.mwg.*;

public class ActionTraverseOrKeep implements Action {

    public static final String NAME = "traverseOrKeep";

    private final String _name;

    public ActionTraverseOrKeep(final String p_name) {
        this._name = p_name;
    }

    @Override
    public final void eval(final TaskContext ctx) {
        final String flatName = ctx.template(_name);
        final TaskResult previousResult = ctx.result();
        if (previousResult != null) {
            final TaskResult finalResult = ctx.newResult();
            final int previousSize = previousResult.size();
            final DeferCounter defer = ctx.graph().newCounter(previousSize);
            for (int i = 0; i < previousSize; i++) {
                final Object loop = previousResult.get(i);
                if (loop instanceof BaseNode) {
                    Node casted = (Node) loop;
                    if (casted.type(flatName) == Type.RELATION) {
                        casted.relation(flatName, new Callback<Node[]>() {
                            @Override
                            public void on(Node[] result) {
                                if (result != null) {
                                    for (int j = 0; j < result.length; j++) {
                                        finalResult.add(result[j]);
                                    }
                                }
                                defer.count();
                            }
                        });
                    } else {
                        finalResult.add(casted.graph().cloneNode(casted));
                        defer.count();
                    }
                } else {
                    //TODO add closable management
                    finalResult.add(loop);
                    defer.count();
                }
            }
            defer.then(new Job() {
                @Override
                public void run() {
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
        TaskHelper.serializeString(_name, builder,true);
        builder.append(Constants.TASK_PARAM_CLOSE);
    }

    @Override
    public String toString() {
        final StringBuilder res = new StringBuilder();
        serialize(res);
        return res.toString();
    }

}