/**
 * Copyright 2017 The MWG Authors.  All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mwg.internal.task;

import org.mwg.Constants;
import org.mwg.Node;
import org.mwg.base.BaseNode;
import org.mwg.task.Action;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskFunctionSelect;
import org.mwg.task.TaskResult;

import javax.script.ScriptContext;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;

class ActionSelect implements Action {

    private final String _script;
    private final TaskFunctionSelect _filter;

    ActionSelect(String script, TaskFunctionSelect filter) {
        this._script = script;
        this._filter = filter;
    }

    @Override
    public void eval(TaskContext ctx) {
        final TaskResult previous = ctx.result();
        final TaskResult next = ctx.newResult();
        final int previousSize = previous.size();

        for (int i = 0; i < previousSize; i++) {
            final Object obj = previous.get(i);
            if (obj instanceof BaseNode) {
                final Node casted = (Node) obj;

                if (_filter != null && _filter.select(casted, ctx)) {
                    next.add(casted);
                } else if (_script != null && callScript(casted, ctx)) {
                    next.add(casted);
                } else {
                    casted.free();
                }
            } else {
                next.add(obj);
            }
        }

        //optimization to avoid the need to clone selected nodes
        previous.clear();
        ctx.continueWith(next);
    }


    /**
     * @native ts
     * var print = console.log;
     * return eval(this._script);
     */
    private boolean callScript(Node node, TaskContext context) {
        ScriptContext scriptCtx = new SimpleScriptContext();
        scriptCtx.setAttribute("node", node, ScriptContext.ENGINE_SCOPE);
        scriptCtx.setAttribute("context", context, ScriptContext.ENGINE_SCOPE);
        try {
            return (boolean) TaskHelper.SCRIPT_ENGINE.eval(_script, scriptCtx);
        } catch (ScriptException | ClassCastException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void serialize(StringBuilder builder) {
        if (_script == null) {
            throw new RuntimeException("Select remote usage not managed yet, please use SelectScript instead !");
        }
        builder.append(CoreActionNames.SELECT);
        builder.append(Constants.TASK_PARAM_OPEN);
        TaskHelper.serializeString(_script, builder,true);
        builder.append(Constants.TASK_PARAM_CLOSE);
    }

    @Override
    public String toString() {
        if (_filter != null) {
            return "select()";
        } else {
            final StringBuilder res = new StringBuilder();
            serialize(res);
            return res.toString();
        }
    }
}
