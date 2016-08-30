package org.mwg.ml.common.structure;

import org.mwg.Callback;
import org.mwg.Graph;
import org.mwg.Node;
import org.mwg.Type;
import org.mwg.ml.common.distance.Distance;
import org.mwg.plugin.AbstractNode;
import org.mwg.plugin.NodeState;
import org.mwg.struct.Relationship;
import org.mwg.task.*;

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
    private static long _VALUES = 7;

    //to store only on the root node
    private static long _PRECISION = 8;
    private static long _DIM = 9;

    public static String BOUNDMIN="boundmin";
    public static String BOUNDMAX="boundmax";
    public static String PRECISION="precision";



    //The beginning of relations navigation
    private static long _RELCONST = 16; //Should be always a power of 2


    /**
     * @native ts
     * var result = Long.UZERO;
     * for(var i = 0; i < centerKey.length; i++) {
     * if(i!=0){
     *     result = result.shiftLeft(1);
     * }
     * if (keyToInsert[i] > centerKey[i]) {
     * result = result.add(Long.ONE);
     * }
     * }
     * return result.add(Long.fromNumber(org.mwg.ml.common.structure.NDTree._RELCONST, true)).toNumber();
     */
    private static long getRelationId(double[] centerKey, double[] keyToInsert) {
        long result = 0;
        for (int i = 0; i < centerKey.length; i++) {
            if(i!=0){
                result = result << 1;
            }
            if (keyToInsert[i] > centerKey[i]) {
                result += 1;
            }
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



    @Override
    public void setProperty(String propertyName, byte propertyType, Object propertyValue) {
        if(propertyName.equals(BOUNDMIN)){
            NodeState state = unphasedState();
            state.set(_BOUNDMIN,Type.DOUBLE_ARRAY,propertyValue);
        }
        else if(propertyName.equals(BOUNDMAX)){
            NodeState state = unphasedState();
            state.set(_BOUNDMAX,Type.DOUBLE_ARRAY,propertyValue);
        }
        else if(propertyName.equals(PRECISION)){
            NodeState state = unphasedState();
            state.set(_PRECISION,Type.DOUBLE_ARRAY,propertyValue);
        }
        else {
            super.setProperty(propertyName, propertyType, propertyValue);
        }
    }


    //Insert key/value task
    private static Task insert = whileDo(new TaskFunctionConditional() {
        @Override
        public boolean eval(TaskContext context) {

            Node current = context.resultAsNodes().get(0);
            NodeState state = current.graph().resolver().resolveState(current);

            //Get state variables here

            double[] boundMax = (double[]) state.get(_BOUNDMAX);
            double[] boundMin = (double[]) state.get(_BOUNDMIN);
            double[] centerKey = new double[boundMax.length];
            for(int i=0;i<centerKey.length;i++){
                centerKey[i]=(boundMax[i]+boundMin[i])/2;
            }

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
                System.out.println(traverseId-_RELCONST);
                //Check if there is a node already in this subspace, otherwise create it
                if (state.get(traverseId) == null) {
                    double[] newBoundMin = new double[dim];
                    double[] newBoundMax = new double[dim];

                    for (int i = 0; i < centerKey.length; i++) {
                        //Update the bounds in a way that they have always minimum precision[i] of width
                        if (keyToInsert[i] <= centerKey[i]) {
                            newBoundMin[i] = boundMin[i];
                            newBoundMax[i] = Math.max(centerKey[i] - boundMin[i], precision[i]) + boundMin[i];

                        } else {
                            newBoundMax[i] = boundMax[i];
                            newBoundMin[i] = boundMax[i] - Math.max(boundMax[i] - centerKey[i], precision[i]);
                        }
                    }
                    //Create the new subspace node
                    NDTree newChild = (NDTree) current.graph().newTypedNode(current.world(), current.time(), NAME);
                    NodeState newState = newChild.graph().resolver().resolveState(newChild);
                    newState.set(_BOUNDMIN, Type.DOUBLE_ARRAY, newBoundMin);
                    newState.set(_BOUNDMAX, Type.DOUBLE_ARRAY, newBoundMax);
                    Relationship relChild = (Relationship) state.getOrCreate(traverseId,Type.RELATION);
                    relChild.add(newChild.id());
                    newChild.free();
                }
                //toDo how to optimzie not to lookup
                //In all cases we can traverse here
                context.setVariable("next", traverseId);
            } else {
                Node valueToInsert = (Node) context.variable("value").get(0);
                //toDo Validate append relationships

                Relationship rel = (Relationship) state.getOrCreate(_VALUES, Type.RELATION);
                rel.add(valueToInsert.id());
            }

            return continueNavigation;
        }

        //todo check how to traverse on long
    }, Actions.action (ActionTraverseById.NAME,"{{next}}"));

    public void insert(final double[] key, final Node value, final Callback<Boolean> callback) {
        NodeState state = unphasedState();

        final double[] precisions = (double[]) state.get(_PRECISION);

//        int dim;
//        try {
//            dim = (int) state.get(_DIM);
//            if(dim==0){
//                dim = key.length;
//                state.set(_DIM,Type.INT,dim);
//            }
//        }
//        catch (Exception ex){
//            dim = key.length;
//            state.set(_DIM,Type.INT,dim);
//        }
//        if (key.length != dim) {
//            throw new RuntimeException("Key size should always be the same");
//        }

        TaskContext tc = insert.prepareWith(graph(), this, new Callback<TaskResult>() {
            @Override
            public void on(TaskResult result) {
                result.free();
                if (callback != null) {
                    callback.on(true);
                }
            }
        });

        TaskResult res = tc.newResult();
        res.add(key);

        //Set global variables
        tc.setGlobalVariable("key", res);
        tc.setGlobalVariable("value", value);

        TaskResult resPres = tc.newResult();
        resPres.add(precisions);
        tc.setGlobalVariable("precision", resPres);

        //Set local variables
        insert.executeUsing(tc);
    }


}