package org.mwg.importer;

import org.mwg.task.Action;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;

class ActionSplit implements Action {

    private String _splitPattern;

    ActionSplit(String p_splitPattern) {
        this._splitPattern = p_splitPattern;
    }

    @Override
    public void eval(TaskContext ctx) {
        final String splitPattern = ctx.template(this._splitPattern);
        TaskResult previous = ctx.result();
        TaskResult next = ctx.wrap(null);
        for (int i = 0; i < previous.size(); i++) {
            final Object loop = previous.get(0);
            if (loop instanceof String) {
                String[] splitted = ((String) loop).split(splitPattern);
                if (previous.size() == 1) {
                    for (int j = 0; j < splitted.length; j++) {
                        next.add(splitted[j]);
                    }
                } else {
                    next.add(splitted);
                }
            }
        }
        ctx.continueWith(next);
    }

    @Override
    public String toString() {
        return "split(\'" + _splitPattern + "\')";
    }

}
