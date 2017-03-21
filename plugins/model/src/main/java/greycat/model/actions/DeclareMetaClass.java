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
import greycat.internal.task.TaskHelper;
import greycat.model.MetaClass;
import greycat.model.MetaModelPlugin;
import greycat.struct.Buffer;

public class DeclareMetaClass implements Action {

    static final String NAME = "declareMetaClass";

    private final String _name;

    DeclareMetaClass(final String name) {
        this._name = name;
    }

    @Override
    public void eval(TaskContext ctx) {
        Graph graph = ctx.graph();
        long world = ctx.world();
        long time = ctx.time();
        ctx.graph().index(ctx.world(), ctx.time(), MetaModelPlugin.INDEXES, new Callback<NodeIndex>() {
            @Override
            public void on(final NodeIndex indexResult) {
                indexResult.graph().resolver().externalLock(indexResult);
                indexResult.find(new Callback<Node[]>() {
                    @Override
                    public void on(Node[] result) {
                        MetaClass finalResult = null;
                        if (result.length == 0) {
                            MetaClass newMetaClass = (MetaClass) graph.newTypedNode(world, time, MetaClass.NAME);
                            newMetaClass.set("name", Type.STRING, _name);
                            indexResult.addToIndex(newMetaClass, "name");
                            finalResult = newMetaClass;
                        } else {
                            finalResult = (MetaClass) result[0];
                        }
                        indexResult.graph().resolver().externalUnlock(indexResult);
                        ctx.continueWith(ctx.wrap(finalResult));
                    }
                }, _name);
            }
        });
    }

    @Override
    public void serialize(final Buffer builder) {
        builder.writeString(NAME);
        builder.writeChar(Constants.TASK_PARAM_OPEN);
        TaskHelper.serializeString(_name, builder, true);
        builder.writeChar(Constants.TASK_PARAM_CLOSE);
    }

}
