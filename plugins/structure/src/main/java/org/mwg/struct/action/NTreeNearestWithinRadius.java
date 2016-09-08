package org.mwg.struct.action;

import org.mwg.Callback;
import org.mwg.DeferCounter;
import org.mwg.Node;
import org.mwg.plugin.AbstractTaskAction;
import org.mwg.plugin.Job;
import org.mwg.struct.NTree;
import org.mwg.struct.tree.KDTree;
import org.mwg.struct.tree.NDTree;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskResult;
import org.mwg.task.TaskResultIterator;

public class NTreeNearestWithinRadius extends AbstractTaskAction {

    public static String NAME = "nTreeNearestWithinRadius";

    private final double[] _key;
    private final double _radius;

    public NTreeNearestWithinRadius(final double[] key, final double radius) {
        super();
        this._key = key;
        this._radius = radius;
    }

    @Override
    public final void eval(final TaskContext context) {
        final TaskResult previousResult = context.result();
        final TaskResult<Node> nextResult = context.newResult();
        if (previousResult != null) {
            final DeferCounter defer = context.graph().newCounter(previousResult.size());
            final TaskResultIterator previousResultIt = previousResult.iterator();
            Object iter = previousResultIt.next();
            while (iter != null) {
                if (iter instanceof KDTree || iter instanceof NDTree) {
                    ((NTree) iter).nearestWithinRadius(_key, _radius, new Callback<Node[]>() {
                        @Override
                        public void on(Node[] result) {
                            for (int i = 0; i < result.length; i++) {
                                nextResult.add(result[i]);
                            }
                            defer.count();
                        }
                    });
                } else {
                    defer.count();
                }
                iter = previousResultIt.next();
            }
            defer.then(new Job() {
                @Override
                public void run() {
                    context.continueWith(nextResult);
                }
            });
        } else {
            context.continueWith(nextResult);
        }
    }

    @Override
    public String toString() {
        return "nTreeNearestWithinRadius(\'" + "\')";
    }

}