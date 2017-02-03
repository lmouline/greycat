/**
 * Copyright 2017 The GreyCat Authors.  All rights reserved.
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
package greycat.internal.task;

import greycat.Constants;
import greycat.Node;
import greycat.task.Action;
import greycat.task.TaskContext;

class ActionCreateNode implements Action {

    private final String _typeNode;

    ActionCreateNode(final String typeNode) {
        this._typeNode = typeNode;
    }

    @Override
    public void eval(final TaskContext ctx) {
        Node newNode;
        if (_typeNode == null) {
            newNode = ctx.graph().newNode(ctx.world(), ctx.time());
        } else {
            String templatedType = ctx.template(_typeNode);
            newNode = ctx.graph().newTypedNode(ctx.world(), ctx.time(), templatedType);
        }
        ctx.continueWith(ctx.wrap(newNode));
    }

    @Override
    public void serialize(StringBuilder builder) {
        if (_typeNode == null) {
            builder.append(CoreActionNames.CREATE_NODE);
            builder.append(Constants.TASK_PARAM_OPEN);
            builder.append(Constants.TASK_PARAM_CLOSE);
        } else {
            builder.append(CoreActionNames.CREATE_TYPED_NODE);
            builder.append(Constants.TASK_PARAM_OPEN);
            TaskHelper.serializeString(_typeNode, builder,true);
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
