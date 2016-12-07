package org.mwg.core.task;

import org.mwg.*;
import org.mwg.base.BaseNode;
import org.mwg.plugin.Job;
import org.mwg.struct.RelationIndexed;
import org.mwg.task.Action;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;

class ActionTraverseOrAttribute implements Action {

    private final String _name;
    private final String[] _params;

    ActionTraverseOrAttribute(final String p_name, final String... p_params) {
        this._name = p_name;
        this._params = p_params;
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
    public String toString() {
        return "traverse(\'" + _name + "\')";
    }
}