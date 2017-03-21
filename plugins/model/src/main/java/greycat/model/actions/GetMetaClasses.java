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
package greycat.model.actions;

import greycat.*;
import greycat.model.MetaModelPlugin;
import greycat.struct.Buffer;

public class GetMetaClasses implements Action {

    static final String NAME = "getMetaClasses";

    @Override
    public void eval(TaskContext ctx) {
        ctx.graph().indexIfExists(ctx.world(), ctx.time(), MetaModelPlugin.INDEXES, new Callback<NodeIndex>() {
            @Override
            public void on(NodeIndex result) {
                if (result == null) {
                    ctx.continueWith(ctx.newResult());
                } else {
                    result.find(new Callback<Node[]>() {
                        @Override
                        public void on(Node[] result) {
                            ctx.continueWith(ctx.wrap(result));
                        }
                    });
                }
            }
        });
    }

    @Override
    public void serialize(final Buffer builder) {
        builder.writeString(NAME);
        builder.writeChar(Constants.TASK_PARAM_OPEN);
        builder.writeChar(Constants.TASK_PARAM_CLOSE);
    }
}
