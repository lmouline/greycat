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
import greycat.plugin.Job;
import greycat.Action;
import greycat.struct.Buffer;
import greycat.struct.RelationIndexed;
import greycat.TaskContext;
import greycat.TaskResult;

class ActionTraverseOrAttribute implements Action {

    private final String _name;
    private final String[] _params;

    private final boolean _isAttribute;
    private final boolean _isUnknown;

    ActionTraverseOrAttribute(final boolean isAttribute, boolean isUnknown, final String p_name, final String... p_params) {
        this._name = p_name;
        this._params = p_params;
        this._isUnknown = isUnknown;
        this._isAttribute = isAttribute;
    }

    @Override
    public final void eval(final TaskContext ctx) {
        final TaskResult finalResult = ctx.newResult();
        final String flatName = ctx.template(_name);
        final TaskResult previousResult = ctx.result();
        if (previousResult != null) {
            final int previousSize = previousResult.size();
            final DeferCounter defer = ctx.graph().newCounter(previousSize);
            for (int i = 0; i < previousSize; i++) {
                final Object loop = previousResult.get(i);
                if (loop instanceof BaseNode) {
                    final Node casted = (Node) loop;
                    switch (casted.type(flatName)) {
                        case Type.RELATION:
                            casted.relation(flatName, new Callback<Node[]>() {
                                @Override
                                public void on(Node[] result) {
                                    if (result != null) {
                                        for (int j = 0; j < result.length; j++) {
                                            finalResult.add(result[j]);
                                        }
                                    }
                                    casted.free();
                                    defer.count();
                                }
                            });
                            break;
                        case Type.RELATION_INDEXED:
                            //TODO move this to the API
                            RelationIndexed relationIndexed = (RelationIndexed) casted.get(flatName);
                            if (relationIndexed != null) {
                                if (_params != null && _params.length > 0) {
                                    String[] templatedParams = ctx.templates(_params);
                                    Query query = ctx.graph().newQuery();
                                    query.setWorld(casted.world());
                                    query.setTime(casted.time());
                                    String previous = null;
                                    for (int k = 0; k < templatedParams.length; k++) {
                                        if (previous != null) {
                                            query.add(previous, templatedParams[k]);
                                            previous = null;
                                        } else {
                                            previous = templatedParams[k];
                                        }
                                    }
                                    relationIndexed.findByQuery(query, new Callback<Node[]>() {
                                        @Override
                                        public void on(Node[] result) {
                                            if (result != null) {
                                                for (int j = 0; j < result.length; j++) {
                                                    if (result[j] != null) {
                                                        finalResult.add(result[j]);
                                                    }
                                                }
                                            }
                                            casted.free();
                                            defer.count();
                                        }
                                    });
                                } else {
                                    casted.graph().lookupAll(ctx.world(), ctx.time(), relationIndexed.all(), new Callback<Node[]>() {
                                        @Override
                                        public void on(Node[] result) {
                                            if (result != null) {
                                                for (int j = 0; j < result.length; j++) {
                                                    if (result[j] != null) {
                                                        finalResult.add(result[j]);
                                                    }
                                                }
                                            }
                                            casted.free();
                                            defer.count();
                                        }
                                    });
                                }
                            } else {
                                defer.count();
                            }
                            break;
                        default:
                            Object resolved = casted.get(flatName);
                            if (resolved != null) {
                                finalResult.add(resolved);
                            }
                            casted.free();
                            defer.count();
                            break;
                    }
                } else {
                    //TODO add closable management
                    finalResult.add(loop);
                    defer.count();
                }
            }
            defer.then(new Job() {
                @Override
                public void run() {
                    //optimization to avoid iterating again on previous result set
                    previousResult.clear();
                    ctx.continueWith(finalResult);
                }
            });
        } else {
            ctx.continueTask();
        }
    }

    @Override
    public void serialize(final Buffer builder) {
        if (_isUnknown) {
            builder.writeString(_name);
        } else {
            if (_isAttribute) {
                builder.writeString(CoreActionNames.ATTRIBUTE);
                builder.writeChar(Constants.TASK_PARAM_OPEN);
                builder.writeString(_name);
                builder.writeChar(Constants.TASK_PARAM_CLOSE);
            } else {
                builder.writeString(CoreActionNames.TRAVERSE);
                builder.writeChar(Constants.TASK_PARAM_OPEN);
                builder.writeString(_name);
                if (_params != null && _params.length > 0) {
                    builder.writeChar(Constants.TASK_PARAM_SEP);
                    TaskHelper.serializeStringParams(_params, builder);
                }
                builder.writeChar(Constants.TASK_PARAM_CLOSE);
            }
        }
    }

}