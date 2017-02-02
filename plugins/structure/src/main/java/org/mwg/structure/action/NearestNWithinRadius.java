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
package org.mwg.structure.action;

import org.mwg.DeferCounter;
import org.mwg.plugin.Job;
import org.mwg.structure.Tree;
import org.mwg.structure.TreeResult;
import org.mwg.structure.trees.KDTree;
import org.mwg.structure.trees.NDTree;
import org.mwg.task.Action;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;
import org.mwg.task.TaskResultIterator;

public class NearestNWithinRadius implements Action {

    public static String NAME = "NearestNWithinRadius";

    private final double[] _key;
    private final int _n;
    private final double _radius;
    private final boolean _fetchNodes;

    public NearestNWithinRadius(final int n, final double radius, final double[] key, final boolean fetchNodes) {
        this._key = key;
        this._n = n;
        this._radius = radius;
        this._fetchNodes=fetchNodes;
    }

    @Override
    public final void eval(final TaskContext ctx) {
        final TaskResult previousResult = ctx.result();
        final TaskResult<Object> nextResult = ctx.newResult();
        if (previousResult != null) {
            final DeferCounter defer = ctx.graph().newCounter(previousResult.size());
            final TaskResultIterator previousResultIt = previousResult.iterator();
            Object iter = previousResultIt.next();
            while (iter != null) {
            if (iter instanceof NDTree || iter instanceof KDTree) {
                    TreeResult tr = ((Tree) iter).nearestNWithinRadius(_key, _n, _radius);

                    if(_fetchNodes){
                        long[] nodeIds = new long[tr.size()];
                        for (int i = 0; i < tr.size(); i++) {
                            nodeIds[i] = tr.value(i);
                        }

                        ctx.graph().lookupAll(ctx.world(), ctx.time(), nodeIds, result -> {
                            for (int j = 0; j < result.length; j++) {
                                TaskResult<Object> line = ctx.newResult();
                                line.add(tr.keys(j));
                                line.add(result[j]);
                                line.add(tr.distance(j));
                                nextResult.add(line);
                            }
                            tr.free();
                            defer.count();
                        });
                    }
                    else{
                        for (int i = 0; i < tr.size(); i++) {
                            TaskResult<Object> line = ctx.newResult();
                            line.add(tr.keys(i));
                            line.add(tr.value(i));
                            line.add(tr.distance(i));
                            nextResult.add(line);
                        }
                        tr.free();
                        defer.count();
                    }
                } else {
                    defer.count();
                }
                iter = previousResultIt.next();
            }
            defer.then(new Job() {
                @Override
                public void run() {
                    ctx.continueWith(nextResult);
                }
            });
        } else {
            ctx.continueWith(nextResult);
        }
    }

    @Override
    public void serialize(StringBuilder builder) {
        throw new RuntimeException("Not supported yet!");
    }

    @Override
    public String toString() {
        return "NearestNWithinRadius(\'" + "\')";
    }

}