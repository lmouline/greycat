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

import org.mwg.Callback;
import org.mwg.Constants;
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
        if (afterTemplate.startsWith("[")) {
            afterTemplate = afterTemplate.substring(1, afterTemplate.length() - 1);
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
    public void serialize(StringBuilder builder) {
        builder.append(CoreActionNames.LOOKUP_ALL);
        builder.append(Constants.TASK_PARAM_OPEN);
        TaskHelper.serializeString(_ids, builder,true);
        builder.append(Constants.TASK_PARAM_CLOSE);
    }

    @Override
    public String toString() {
        final StringBuilder res = new StringBuilder();
        serialize(res);
        return res.toString();
    }


}
