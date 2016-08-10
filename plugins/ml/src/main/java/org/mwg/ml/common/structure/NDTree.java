package org.mwg.ml.common.structure;

import org.mwg.Graph;
import org.mwg.Node;
import org.mwg.Type;
import org.mwg.plugin.AbstractNode;
import org.mwg.plugin.NodeState;
import org.mwg.task.Task;
import org.mwg.task.TaskContext;
import org.mwg.task.TaskFunctionConditional;

import static org.mwg.task.Actions.traverse;
import static org.mwg.task.Actions.whileDo;

/**
 * Created by assaad on 09/08/16.
 */
public class NDTree extends AbstractNode {
    public static final String NAME = "NDTree";

    public NDTree(long p_world, long p_time, long p_id, Graph p_graph) {
        super(p_world, p_time, p_id, p_graph);
    }

    //To store in every node in the tree
    //First, gaussian related stuff
    private static long _TOTAL = 0;
    private static long _SUM = 1;
    private static long _SUMSQ = 2;
    private static long _MIN = 3;
    private static long _MAX = 4;
    //Second, subspace related stuff
    private static long _BOUNDMIN = 5;
    private static long _BOUNDMAX = 6;
    private static long _CENTER = 7;
    private static long _VALUES=8;

    //to store only on the root node
    private static long _PRECISION = 9;
    private static long _DIM = 10;

    //The beginning of relations navigation
    private static long _RELCONST = 16; //Should be always a power of 2


    /**
     * @native ts
     * var result = Long.UZERO;
     * for(var i = 0; i < centerKey.length; i++) {
     * if (keyToInsert[i] <= centerKey[i]) {
     * result = result.add(Long.ONE);
     * }
     * result = result.shiftLeft(1);
     * }
     * return result.add(Long.fromNumber(org.mwg.ml.common.structure.NDTree._RELCONST, true)).toNumber();
     */
    private static long getRelationId(double[] centerKey, double[] keyToInsert) {
        long result = 0;
        for (int i = 0; i < centerKey.length; i++) {
            if (keyToInsert[i] <= centerKey[i]) {
                result += 1;
            }
            result = result << 2;
        }
        return result + _RELCONST;
    }


    private static void updateGaussian(NodeState state, double[] key) {
        int total = 0;
        Integer x = (Integer) state.get(_TOTAL);
        if (x != null) {
            total = x;
        }
        if (total == 0) {
            state.set(_TOTAL, Type.INT, 1);
            state.set(_SUM, Type.DOUBLE_ARRAY, key);
        } else {
            int features = key.length;
            double[] sum;
            double[] min;
            double[] max;
            double[] sumsquares;

            //Upgrade dirac to gaussian
            if (total == 1) {
                //Create getMin, getMax, sumsquares
                sum = (double[]) state.get(_SUM);
                min = new double[features];
                max = new double[features];
                System.arraycopy(sum, 0, min, 0, features);
                System.arraycopy(sum, 0, max, 0, features);
                sumsquares = new double[features * (features + 1) / 2];
                int count = 0;
                for (int i = 0; i < features; i++) {
                    for (int j = i; j < features; j++) {
                        sumsquares[count] = sum[i] * sum[j];
                        count++;
                    }
                }
            }
            //Otherwise, get previously stored values
            else {
                sum = (double[]) state.get(_SUM);
                min = (double[]) state.get(_MIN);
                max = (double[]) state.get(_MAX);
                sumsquares = (double[]) state.get(_SUMSQ);
            }

            //Update the values
            for (int i = 0; i < features; i++) {
                if (key[i] < min[i]) {
                    min[i] = key[i];
                }

                if (key[i] > max[i]) {
                    max[i] = key[i];
                }
                sum[i] += key[i];
            }

            int count = 0;
            for (int i = 0; i < features; i++) {
                for (int j = i; j < features; j++) {
                    sumsquares[count] += key[i] * key[j];
                    count++;
                }
            }
            total++;
            //Store everything
            state.set(_TOTAL, Type.DOUBLE_ARRAY, total);
            state.set(_SUM, Type.DOUBLE_ARRAY, sum);
            state.set(_MIN, Type.DOUBLE_ARRAY, min);
            state.set(_MAX, Type.DOUBLE_ARRAY, max);
            state.set(_SUMSQ, Type.DOUBLE_ARRAY, sumsquares);
        }
    }


    //Insert key/value task
    private static Task insert = whileDo(new TaskFunctionConditional() {
        @Override
        public boolean eval(TaskContext context) {

            Node current = context.resultAsNodes().get(0);
            NodeState state = current.graph().resolver().resolveState(current);

            //Get state variables here
            double[] centerKey = (double[]) state.get(_CENTER);
            double[] boundMax = (double[]) state.get(_BOUNDMAX);
            double[] boundMin = (double[]) state.get(_BOUNDMIN);


            //Get variables from context
            //toDo optimize the variables here
            double[] keyToInsert = (double[]) context.variable("key").get(0);

            double[] precision = (double[]) context.variable("precision").get(0);
            int dim = keyToInsert.length;

            //Update the gaussian of the current node in all cases
            updateGaussian(state, keyToInsert);

            //Check if we can go deeper or not:
            boolean continueNavigation = false;
            for (int i = 0; i < dim; i++) {
                if (boundMax[i] - boundMin[i] > precision[i]) {
                    continueNavigation = true;
                    break;
                }
            }


            if (continueNavigation) {
                //Set the long to traverse
                long traverseId = getRelationId(centerKey, keyToInsert);
                //Check if there is a node already in this subspace, otherwise create it
                if (state.get(traverseId) == null) {
                    double[] newBoundMin = new double[dim];
                    double[] newBoundMax = new double[dim];
                    double[] newCenterKey = new double[dim];

                    for (int i = 0; i < centerKey.length; i++) {
                        //Update the bounds in a way that they have always minimum precision[i] of width
                        if (keyToInsert[i] <= centerKey[i]) {
                            newBoundMin[i] = boundMin[i];
                            newBoundMax[i] = Math.max(centerKey[i] - boundMin[i], precision[i]) + boundMin[i];

                        } else {
                            newBoundMax[i] = boundMax[i];
                            newBoundMin[i] = boundMax[i] - Math.max(boundMax[i] - centerKey[i], precision[i]);
                        }
                        //The center is always in the middle of the subspace
                        newCenterKey[i] = (newBoundMax[i] + newBoundMin[i]) / 2;
                    }
                    //Create the new subspace node
                    NDTree newChild = (NDTree) current.graph().newTypedNode(current.world(), current.time(), NAME);
                    NodeState newState = newChild.graph().resolver().resolveState(newChild);
                    newState.set(_CENTER,Type.DOUBLE_ARRAY,newCenterKey);
                    newState.set(_BOUNDMIN,Type.DOUBLE_ARRAY,newBoundMin);
                    newState.set(_BOUNDMAX,Type.DOUBLE_ARRAY,newBoundMax);
                    newChild.free();
                }
                //toDo how to optimzie not to lookup
                //In all cases we can traverse here
                context.setVariable("next",traverseId);
            }
            else{
                Node valueToInsert = (Node) context.variable("value").get(0);
                //toDo Validate append relationships
                state.append(_VALUES,Type.RELATION,valueToInsert.id());
            }

            return continueNavigation;
        }

        //todo check how to traverse on long
    }, traverse("{{next}}"));


}
