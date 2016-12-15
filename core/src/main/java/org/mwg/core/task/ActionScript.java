package org.mwg.core.task;

import org.mwg.Constants;
import org.mwg.task.Action;
import org.mwg.task.TaskContext;

import javax.script.*;

public class ActionScript implements Action {

    private String _script;

    ActionScript(String script) {
        this._script = script;
    }

    /**
     * @native ts
     * var print = console.log;
     * eval(this._script);
     */
    @Override
    public void eval(TaskContext ctx) {
        ScriptContext scriptCtx = new SimpleScriptContext();
        scriptCtx.setAttribute("ctx", ctx, ScriptContext.ENGINE_SCOPE);
        try {
            TaskHelper.SCRIPT_ENGINE.eval(this._script, scriptCtx);
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void serialize(StringBuilder builder) {
        builder.append(ActionNames.SCRIPT);
        builder.append(Constants.TASK_PARAM_OPEN);
        builder.append("\"");
        builder.append(_script);
        builder.append("\"");
        builder.append(Constants.TASK_PARAM_CLOSE);
    }

    @Override
    public String toString() {
        final StringBuilder res = new StringBuilder();
        serialize(res);
        return res.toString();
    }

}



