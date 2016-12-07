package org.mwg.core.task;

import org.mwg.Callback;
import org.mwg.Node;
import org.mwg.task.Action;
import org.mwg.task.TaskContext;

class ActionLookupAll implements Action {

    private final String _ids;

    ActionLookupAll(final String p_ids) {
        this._ids = p_ids;
    }

    @Override
    public void eval(final TaskContext ctx) {
        String afterTemplate = ctx.template(_ids).trim();
        if(afterTemplate.startsWith("[")){
            afterTemplate = afterTemplate.substring(1,afterTemplate.length()-1);
        }
        String[] values = afterTemplate.split(",");
        long[] ids = new long[values.length];
        for (int i = 0; i < values.length; i++) {
            ids[i] = Long.parseLong(values[i]);
        }
        ctx.graph().lookupAll(ctx.world(), ctx.time(), ids, new Callback<Node[]>() {
            @Override
            public void on(Node[] result) {
                ctx.continueWith(ctx.wrap(result));
            }
        });
    }

    @Override
    public String toString() {
        return "lookup(\'" + _ids + "\")";
    }

}
