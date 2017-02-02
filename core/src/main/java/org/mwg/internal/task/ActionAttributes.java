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
import org.mwg.Node;
import org.mwg.Type;
import org.mwg.base.BaseNode;
import org.mwg.plugin.NodeState;
import org.mwg.plugin.NodeStateCallback;
import org.mwg.task.Action;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;

class ActionAttributes implements Action {

    private final byte _filter;

    ActionAttributes(String filterType) {
        if (filterType != null) {
            _filter = Type.typeFromName(filterType);
        } else {
            _filter = (byte) -1;
        }
    }

    @Override
    public void eval(TaskContext ctx) {
        final TaskResult previous = ctx.result();
        final TaskResult result = ctx.newResult();
        for (int i = 0; i < previous.size(); i++) {
            if (previous.get(i) instanceof BaseNode) {
                final Node n = (Node) previous.get(i);
                final NodeState nState = ctx.graph().resolver().resolveState(n);
                nState.each(new NodeStateCallback() {
                    @Override
                    public void on(int attributeKey, byte elemType, Object elem) {
                        if (_filter == -1 || elemType == _filter) {
                            String retrieved = ctx.graph().resolver().hashToString(attributeKey);
                            if (retrieved != null) {
                                result.add(retrieved);
                            } else {
                                result.add(attributeKey);
                            }
                        }
                    }
                });
                n.free();
            }
        }
        previous.clear();
        ctx.continueWith(result);
    }

    @Override
    public void serialize(StringBuilder builder) {
        if (_filter == -1) {
            builder.append(CoreActionNames.ATTRIBUTES);
            builder.append(Constants.TASK_PARAM_OPEN);
            builder.append(Constants.TASK_PARAM_CLOSE);
        } else {
            builder.append(CoreActionNames.ATTRIBUTES_WITH_TYPE);
            builder.append(Constants.TASK_PARAM_OPEN);
            TaskHelper.serializeType(_filter, builder);
            builder.append(Constants.TASK_PARAM_CLOSE);
        }
    }

    @Override
    public String toString() {
        final StringBuilder res = new StringBuilder();
        serialize(res);
        return res.toString();
    }

}
