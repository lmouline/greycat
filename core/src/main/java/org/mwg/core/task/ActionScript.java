package org.mwg.core.task;

import org.mwg.Constants;
import org.mwg.task.Action;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;
import org.mwg.utility.Tuple;

import javax.script.*;
import java.io.IOException;
import java.io.Writer;

public class ActionScript implements Action {

    private final String _script;
    private final boolean _async;

    ActionScript(final String script, final boolean async) {
        this._script = script;
        this._async = async;
    }

    /**
     * @native ts
     * var print = function(v){ctx.append(v+'\n');};
     * var variables = ctx.var
     * eval(this._script);
     */
    @Override
    public void eval(TaskContext ctx) {
        final ScriptContext scriptCtx = new SimpleScriptContext();
        final Tuple<String, TaskResult>[] variables = ctx.variables();
        for (int i = 0; i < variables.length; i++) {
            scriptCtx.setAttribute(variables[i].left(), variables[i].right(), ScriptContext.ENGINE_SCOPE);
        }
        scriptCtx.setWriter(new Writer() {
            @Override
            public void write(char[] cbuf, int off, int len) throws IOException {
                ctx.append(new String(cbuf, off, len));
            }

            @Override
            public void flush() throws IOException {
                //noop
            }

            @Override
            public void close() throws IOException {
                //noop
            }
        });
        //protect scope if !async
        scriptCtx.setAttribute("ctx", ctx, ScriptContext.ENGINE_SCOPE);
        scriptCtx.setAttribute("result", ctx.result(), ScriptContext.ENGINE_SCOPE);
        try {
            Object result = TaskHelper.SCRIPT_ENGINE.eval(this._script, scriptCtx);
            if (!_async) {
                if (result != null) {
                    ctx.continueWith(ctx.wrap(result));
                } else {
                    ctx.continueTask();
                }
            }
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void serialize(StringBuilder builder) {
        if (_async) {
            builder.append(ActionNames.ASYNC_SCRIPT);
        } else {
            builder.append(ActionNames.SCRIPT);
        }
        builder.append(Constants.TASK_PARAM_OPEN);
        TaskHelper.serializeString(_script, builder, true);
        builder.append(Constants.TASK_PARAM_CLOSE);
    }

    @Override
    public String toString() {
        final StringBuilder res = new StringBuilder();
        serialize(res);
        return res.toString();
    }

}



