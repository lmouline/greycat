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

import org.mwg.Callback;
import org.mwg.Constants;
import org.mwg.NodeIndex;
import org.mwg.task.Action;
import org.mwg.task.TaskContext;

class ActionGlobalIndex implements Action {

    private final String _name;

    ActionGlobalIndex(final String p_indexName) {
        if (p_indexName == null) {
            throw new RuntimeException("indexName should not be null");
        }
        _name = p_indexName;
    }

    @Override
    public void eval(final TaskContext ctx) {
        final String name = ctx.template(_name);
        ctx.graph().indexIfExists(ctx.world(), ctx.time(), name, new Callback<NodeIndex>() {
            @Override
            public void on(NodeIndex resolvedIndex) {
                if (resolvedIndex != null) {
                    ctx.continueWith(ctx.wrap(resolvedIndex));
                } else {
                    ctx.continueWith(ctx.newResult());
                }
            }
        });
    }

    @Override
    public void serialize(StringBuilder builder) {
        builder.append(CoreActionNames.GLOBAL_INDEX);
        builder.append(Constants.TASK_PARAM_OPEN);
        builder.append(Constants.TASK_PARAM_CLOSE);
    }

    @Override
    public String toString() {
        final StringBuilder res = new StringBuilder();
        serialize(res);
        return res.toString();
    }

}
