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
package org.mwg.importer;

import org.mwg.Constants;
import org.mwg.internal.task.TaskHelper;
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
    public void serialize(StringBuilder builder) {
        builder.append("split");
        builder.append(Constants.TASK_PARAM_OPEN);
        TaskHelper.serializeString(_splitPattern, builder,true);
        builder.append(Constants.TASK_PARAM_CLOSE);
    }

    @Override
    public String toString() {
        final StringBuilder res = new StringBuilder();
        serialize(res);
        return res.toString();
    }

}
