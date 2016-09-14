package org.mwg.core.task;

import org.mwg.Callback;
import org.mwg.DeferCounter;
import org.mwg.Node;
import org.mwg.Query;
import org.mwg.core.CoreConstants;
import org.mwg.plugin.AbstractNode;
import org.mwg.plugin.AbstractTaskAction;
import org.mwg.plugin.Job;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;

import java.util.Arrays;

class ActionTraverseIndex extends AbstractTaskAction {
    private String _indexName;
    private String[] _queryParams;
    private String[] _resolvedQueryParams;

    ActionTraverseIndex(final String indexName, final String... queryParams) {
        super();
        this._queryParams = queryParams;
        this._indexName = indexName;
        _resolvedQueryParams = new String[queryParams.length];
    }

    @Override
    public void eval(final TaskContext context) {
        final TaskResult finalResult = context.wrap(null);
        final String flatName = context.template(_indexName);

        //template execution, storage for later print
        for(int i = 0; i < _queryParams.length; i++) {
            _resolvedQueryParams[i] = context.template(_queryParams[i]);
        }

        final Query query = context.graph().newQuery();
        query.setWorld(context.world());
        query.setTime(context.time());
        query.setIndexName(flatName);
        for(int i = 0; i < _resolvedQueryParams.length; i = i+2) {
            query.add(_resolvedQueryParams[i], _resolvedQueryParams[i+1]);
        }
        //final String flatQuery = context.template(_query);
        final TaskResult previousResult = context.result();
        if (previousResult != null) {
            final int previousSize = previousResult.size();
            final DeferCounter defer = context.graph().newCounter(previousSize);
            for (int i = 0; i < previousSize; i++) {
                final Object loop = previousResult.get(i);
                if (loop instanceof AbstractNode) {
                    final Node casted = (Node) loop;
                    casted.findByQuery(query, new Callback<Node[]>() {
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
                    //TODO add closable management
                    finalResult.add(loop);
                    defer.count();
                }
            }
            defer.then(new Job() {
                @Override
                public void run() {
                    //optimization to avoid iteration on previous result for free
                    previousResult.clear();
                    context.continueWith(finalResult);
                }
            });
        } else {
            context.continueTask();
        }
    }

    @Override
    public String toString() {
        return "traverseIndex(\'" + _indexName + CoreConstants.QUERY_SEP + String.join(",", _resolvedQueryParams) + "\')";
    }

}

