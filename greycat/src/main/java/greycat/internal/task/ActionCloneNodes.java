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

import greycat.*;
import greycat.base.BaseNode;
import greycat.struct.Buffer;

class ActionCloneNodes implements Action {

    @Override
    public void eval(final TaskContext ctx) {

        TaskResult<Node> previousResult = ctx.resultAsNodes();
        Node[] previousNodes = (Node[]) previousResult.asArray();
        Node[] clones = new Node[previousNodes.length];
        for (int i = 0; i < previousNodes.length; i++) {
            clones[i] = ((BaseNode) previousNodes[i]).createClone();
        }
        previousResult.free();
        ctx.continueWith(ctx.wrap(clones));
    }

    @Override
    public void serialize(final Buffer builder) {
        builder.writeString(CoreActionNames.CLONE_NODES);
        builder.writeChar(Constants.TASK_PARAM_OPEN);
        builder.writeChar(Constants.TASK_PARAM_CLOSE);
    }


    @Override
    public final String name() {
        return CoreActionNames.CLONE_NODES;
    }

}
