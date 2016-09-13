package org.mwg.structure.tree;

import org.mwg.Callback;
import org.mwg.Graph;
import org.mwg.Node;
import org.mwg.Type;
import org.mwg.plugin.AbstractNode;
import org.mwg.plugin.NodeState;
import org.mwg.plugin.NodeStateCallback;
import org.mwg.struct.Relationship;
import org.mwg.structure.action.TraverseById;
import org.mwg.structure.distance.Distance;
import org.mwg.structure.distance.EuclideanDistance;
import org.mwg.structure.util.NDResult;
import org.mwg.structure.util.NearestNeighborList;
import org.mwg.task.*;


import static org.mwg.task.Actions.*;

/**
 * Created by assaad on 09/08/16.
 */
public class NDTree extends AbstractNode {
    public static final String NAME = "NDTree";

    public NDTree(long p_world, long p_time, long p_id, Graph p_graph) {
        super(p_world, p_time, p_id, p_graph);
    }

    //To store only on root node
    private static long _STAT = 0;

    //Gaussian related stats on each subspace node
    private static long _TOTAL = 1;
    private static long _SUM = 2;
    private static long _SUMSQ = 3;
//    private static long _MIN = 4;  //not needed because the node has boundmin and boundmax
//    private static long _MAX = 5;   //not needed because the node has boundmin and boundmax


    // Subspace related stuff
    private static long _BOUNDMIN = 6;
    private static long _BOUNDMAX = 7;
    private static long _VALUES = 8;

    //to store only on the root node
    private static long _PRECISION = 9;
    private static int _NUMNODES = 10;
    private static int _DIM = 11;


    public static boolean STAT_DEF = false;

    public static String BOUNDMIN = "boundmin";
    public static String BOUNDMAX = "boundmax";
    public static String PRECISION = "precision";


    //The beginning of relations navigation
    static long _RELCONST = 16; //Should be always a power of 2


    //region Gaussian and stat related code for each node subspace
    public void setUpdateStat(boolean value) {
        NodeState state = unphasedState();
        state.set(_STAT, Type.BOOL, value);
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
//            double[] min;
//            double[] max;
            double[] sumsquares;

            //Upgrade dirac to gaussian
            if (total == 1) {
                //Create getMin, getMax, sumsquares
                sum = (double[]) state.get(_SUM);
//                min = new double[features];
//                max = new double[features];
//                System.arraycopy(sum, 0, min, 0, features);
//                System.arraycopy(sum, 0, max, 0, features);
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
//                min = (double[]) state.get(_MIN);
//                max = (double[]) state.get(_MAX);
                sumsquares = (double[]) state.get(_SUMSQ);
            }

            //Update the values
            for (int i = 0; i < features; i++) {
//                if (key[i] < min[i]) {
//                    min[i] = key[i];
//                }
//
//                if (key[i] > max[i]) {
//                    max[i] = key[i];
//                }
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
            state.set(_TOTAL, Type.INT, total);
            state.set(_SUM, Type.DOUBLE_ARRAY, sum);
//            state.set(_MIN, Type.DOUBLE_ARRAY, min);
//            state.set(_MAX, Type.DOUBLE_ARRAY, max);
            state.set(_SUMSQ, Type.DOUBLE_ARRAY, sumsquares);
        }
    }

    public int getTotal() {
        Integer x = (Integer) super.getByIndex(_TOTAL);
        if (x == null) {
            return 0;
        } else {
            return x;
        }
    }

    public double[] getAvg() {
        int total = getTotal();
        if (total == 0) {
            return null;
        }
        if (total == 1) {
            return (double[]) super.getByIndex(_SUM);
        } else {
            double[] avg = (double[]) super.getByIndex(_SUM);
            for (int i = 0; i < avg.length; i++) {
                avg[i] = avg[i] / total;
            }
            return avg;
        }

    }

    public double[] getCovarianceArray(double[] avg, double[] err) {
        if (avg == null) {
            double[] errClone = new double[err.length];
            System.arraycopy(err, 0, errClone, 0, err.length);
            return errClone;
        }
        if (err == null) {
            err = new double[avg.length];
        }
        int features = avg.length;

        int total = getTotal();
        if (total == 0) {
            return null;
        }
        if (total > 1) {
            double[] covariances = new double[features];
            double[] sumsquares = (double[]) super.getByIndex(_SUMSQ);

            double correction = total;
            correction = correction / (total - 1);

            int count = 0;
            for (int i = 0; i < features; i++) {
                covariances[i] = (sumsquares[count] / total - avg[i] * avg[i]) * correction;
                if (covariances[i] < err[i]) {
                    covariances[i] = err[i];
                }
                count += features - i;
            }
            return covariances;
        } else {
            double[] errClone = new double[err.length];
            System.arraycopy(err, 0, errClone, 0, err.length);
            return errClone;
        }
    }

    //endregion


    /**
     * @native ts
     * var result = Long.UZERO;
     * for(var i = 0; i < centerKey.length; i++) {
     * if(i!=0){
     * result = result.shiftLeft(1);
     * }
     * if (keyToInsert[i] > centerKey[i]) {
     * result = result.add(Long.ONE);
     * }
     * }
     * return result.add(Long.fromNumber(org.mwg.structure.tree.NDTree._RELCONST, true)).toNumber();
     */
    static long getRelationId(double[] centerKey, double[] keyToInsert) {
        long result = 0;
        for (int i = 0; i < centerKey.length; i++) {
            if (i != 0) {
                result = result << 1;
            }
            if (keyToInsert[i] > centerKey[i]) {
                result += 1;
            }
        }
        return result + _RELCONST;
    }

    static boolean[] binaryFromLong(final long value, final int dim) {
        long tempvalue = value - _RELCONST;
        long shiftvalue = tempvalue >> 1;
        boolean[] res = new boolean[dim];
        for (int i = 0; i < dim; i++) {
            res[dim - i - 1] = ((tempvalue - (shiftvalue << 1)) == 1);
            tempvalue = shiftvalue;
            shiftvalue = tempvalue >> 1;
        }
        return res;
    }

    @Override
    public void setProperty(String propertyName, byte propertyType, Object propertyValue) {
        if (propertyName.equals(BOUNDMIN)) {
            NodeState state = unphasedState();
            state.set(_BOUNDMIN, Type.DOUBLE_ARRAY, propertyValue);
        } else if (propertyName.equals(BOUNDMAX)) {
            NodeState state = unphasedState();
            state.set(_BOUNDMAX, Type.DOUBLE_ARRAY, propertyValue);
        } else if (propertyName.equals(PRECISION)) {
            NodeState state = unphasedState();
            state.set(_PRECISION, Type.DOUBLE_ARRAY, propertyValue);
        } else {
            super.setProperty(propertyName, propertyType, propertyValue);
        }
    }


    //Insert key/value task
    private static Task insert = whileDo(new TaskFunctionConditional() {
        @Override
        public boolean eval(TaskContext context) {

            Node root = (Node) context.variable("root").get(0);

            Node current = context.resultAsNodes().get(0);
            NodeState state = current.graph().resolver().resolveState(current);
            boolean updateStat = (boolean) context.variable("updatestat").get(0);

            //Get state variables here

            double[] boundMax = (double[]) state.get(_BOUNDMAX);
            double[] boundMin = (double[]) state.get(_BOUNDMIN);
            double[] centerKey = new double[boundMax.length];
            for (int i = 0; i < centerKey.length; i++) {
                centerKey[i] = (boundMax[i] + boundMin[i]) / 2;
            }

            //Get variables from context
            //toDo optimize the variables here
            double[] keyToInsert = (double[]) context.variable("key").get(0);

            double[] precision = (double[]) context.variable("precision").get(0);
            int dim = keyToInsert.length;

            //Update the gaussian of the current node
            if (updateStat) {
                updateGaussian(state, keyToInsert);
            }

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
                // System.out.println(traverseId-_RELCONST);
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
                            newBoundMin[i] = boundMax[i] - Math.max(boundMax[i] - centerKey[i], precision[i]);
                            newBoundMax[i] = boundMax[i];
                        }
                    }
                    //Create the new subspace node
                    NDTree newChild = (NDTree) current.graph().newTypedNode(current.world(), current.time(), NAME);
                    NodeState newState = newChild.graph().resolver().resolveState(newChild);
                    newState.set(_BOUNDMIN, Type.DOUBLE_ARRAY, newBoundMin);
                    newState.set(_BOUNDMAX, Type.DOUBLE_ARRAY, newBoundMax);
                    Relationship relChild = (Relationship) state.getOrCreate(traverseId, Type.RELATION);
                    relChild.add(newChild.id());
                    newChild.free();
                    if (root.getByIndex(_NUMNODES) != null) {
                        int count = (int) root.getByIndex(_NUMNODES);
                        count++;
                        root.setPropertyByIndex(_NUMNODES, Type.INT, count);
                    } else {
                        root.setPropertyByIndex(_NUMNODES, Type.INT, 2);
                    }
                }
                //toDo how to optimize not to lookup
                //In all cases we can traverse here
                context.setVariable("next", traverseId);
            } else {
                Node valueToInsert = (Node) context.variable("value").get(0);
                //toDo Validate append relationships
                Relationship rel = (Relationship) state.getOrCreate(_VALUES, Type.RELATION);
                rel.add(valueToInsert.id());
                //In case we want an reverse relationship we should set it here
            }

            return continueNavigation;
        }

        //todo check how to traverse on long
    }, Actions.action(TraverseById.NAME, "{{next}}"));

    public void insert(final double[] key, final Node value, final Callback<Boolean> callback) {
        NodeState state = unphasedState();
        final double[] precisions = (double[]) state.get(_PRECISION);

        if (state.get(_DIM) == null) {
            state.set(_DIM, Type.INT, key.length);
        }

        final int dim = state.getWithDefault(_DIM, key.length);

        if (key.length != dim) {
            throw new RuntimeException("Key size should always be the same");
        }


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
        if (value != null) {
            tc.setGlobalVariable("value", value);
        }

        final boolean updateStat = (boolean) state.getWithDefault(_STAT, STAT_DEF);
        tc.setGlobalVariable("updatestat", updateStat);

        tc.setGlobalVariable("root", this);
        TaskResult resPres = tc.newResult();
        resPres.add(precisions);
        tc.setGlobalVariable("precision", resPres);

        //Set local variables
        insert.executeUsing(tc);
    }


    public void nearestN(final double[] key, final int n, final Callback<Node[]> callback) {
        NodeState state = unphasedState();
        int dim;
        Object tdim = state.get(_DIM);
        if (tdim == null) {
            callback.on(null);
            return;
        } else {
            dim = (int) tdim;
            if (key.length != dim) {
                throw new RuntimeException("Key size should always be the same");
            }
        }


        final NearestNeighborList nnl = new NearestNeighborList(n);
        Distance distance = EuclideanDistance.instance();


        TaskContext tc = nearestTask.prepareWith(graph(), this, new Callback<TaskResult>() {
            @Override
            public void on(TaskResult result) {

                //ToDo replace by lookupAll later
                long[] res = nnl.getAllNodes();

                Task lookupall = setWorld(String.valueOf(world())).setTime(String.valueOf(time())).fromVar("res").foreach(lookup("{{result}}"));
                TaskContext tc = lookupall.prepareWith(graph(), null, new Callback<TaskResult>() {
                    @Override
                    public void on(TaskResult result) {

                        final Node[] finalres = new Node[result.size()];
                        /*for (int i = 0; i < result.size(); i++) {
                            finalres[i] = (Node) result.get(i);
                        }*/
                        callback.on(finalres);
                    }
                });

                TaskResult tr = tc.wrap(res);
                tc.addToGlobalVariable("res", tr);
                lookupall.executeUsing(tc);
            }
        });


        TaskResult res = tc.newResult();
        res.add(key);

        // (this, distance, key, hr, max_dist_sqd, 0, dim, err, nnl);

        tc.setGlobalVariable("key", res);
        tc.setGlobalVariable("distance", distance);
        tc.setGlobalVariable("dim", dim);
        tc.defineVariable("lev", 0);

        nearestTask.executeUsing(tc);
    }


    static double convertToDistance(final long attributeKey, final double[] target, final double[] center, final double[] boundMin, final double[] boundMax, final double[] precision, final Distance distance) {
        double[] childCenter = new double[center.length];
        double minchild = 0;
        double maxchild = 0;
        boolean[] binaries = binaryFromLong(attributeKey, center.length);
        for (int i = 0; i < childCenter.length; i++) {

            if (!binaries[i]) {
                minchild = boundMin[i];
                maxchild = Math.max(center[i] - boundMin[i], precision[i]) + boundMin[i];

            } else {
                minchild = boundMax[i] - Math.max(boundMax[i] - center[i], precision[i]);
                maxchild = boundMax[i];
            }
            childCenter[i] = (minchild + maxchild) / 2;
        }
        return distance.measure(childCenter, target);
    }


    private static Task nearestTask = then(new Action() {
        @Override
        public void eval(TaskContext context) {
            NDTree current = (NDTree) context.result().get(0);
            NodeState state = current.graph().resolver().resolveState(current);

            double[] boundMax = (double[]) state.get(_BOUNDMAX);
            double[] boundMin = (double[]) state.get(_BOUNDMIN);
            double[] centerKey = new double[boundMax.length];
            for (int i = 0; i < centerKey.length; i++) {
                centerKey[i] = (boundMax[i] + boundMin[i]) / 2;
            }
            final double[] target = (double[]) context.variable("key").get(0);
            final Distance distance = (Distance) context.variable("distance").get(0);
            double d = distance.measure(target, centerKey);
            context.setVariable("parentdistance", d);
            context.continueTask();
        }
    })
            .asVar("parent")
            .print("{{result}}")
            .propertiesWithTypes(Type.RELATION)
            .foreach(ifThen(new TaskFunctionConditional() {
                        @Override
                        public boolean eval(TaskContext context) {
                            return true;
                        }
                    }, print("{{result}}"))
            );


    public int getNumNodes() {

        if (getByIndex(_NUMNODES) != null) {
            return (int) getByIndex(_NUMNODES);
        } else {
            return 1;
        }
    }
}