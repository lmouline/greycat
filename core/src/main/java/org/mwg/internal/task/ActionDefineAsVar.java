/**
 * Copyright 2017 The MWG Authors.  All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mwg.internal.task;

import org.mwg.Constants;
import org.mwg.task.Action;
import org.mwg.task.TaskContext;

class ActionDefineAsVar implements Action {

    private final String _name;
    private final boolean _global;

    ActionDefineAsVar(final String p_name, final boolean p_global) {
        if (p_name == null) {
            throw new RuntimeException("name should not be null");
        }
        this._name = p_name;
        this._global = p_global;
    }

    @Override
    public void eval(final TaskContext ctx) {
        if (_global) {
            ctx.setGlobalVariable(ctx.template(_name), ctx.result());
        } else {
            ctx.defineVariable(ctx.template(_name), ctx.result());
        }
        ctx.continueTask();
    }

    @Override
    public void serialize(StringBuilder builder) {
        if (_global) {
            builder.append(CoreActionNames.DEFINE_AS_GLOBAL_VAR);
        } else {
            builder.append(CoreActionNames.DEFINE_AS_VAR);
        }
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
