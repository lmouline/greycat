package org.mwg.importer;

import org.mwg.Constants;
import org.mwg.core.task.TaskHelper;
import org.mwg.importer.util.IterableLines;
import org.mwg.task.Action;
import org.mwg.task.TaskContext;

class ActionReadLines implements Action {

    private final String _pathOrTemplate;

    ActionReadLines(final String p_pathOrTemplate) {
        this._pathOrTemplate = p_pathOrTemplate;
    }

    @Override
    public void eval(final TaskContext ctx) {
        final String path = ctx.template(_pathOrTemplate);
        ctx.continueWith(new IterableLines(path));
    }

    @Override
    public void serialize(StringBuilder builder) {
        builder.append("readLines");
        builder.append(Constants.TASK_PARAM_OPEN);
        TaskHelper.serializeString(_pathOrTemplate, builder);
        builder.append(Constants.TASK_PARAM_CLOSE);
    }

    @Override
    public String toString() {
        final StringBuilder res = new StringBuilder();
        serialize(res);
        return res.toString();
    }
}
