package org.mwg.structure.tree;

import org.mwg.Graph;
import org.mwg.Type;
import org.mwg.base.BaseNode;
import org.mwg.plugin.NodeState;
import org.mwg.plugin.NodeStateCallback;
import org.mwg.struct.DMatrix;
import org.mwg.struct.EGraph;
import org.mwg.struct.ENode;
import org.mwg.struct.LMatrix;
import org.mwg.structure.Tree;
import org.mwg.structure.TreeResult;
import org.mwg.structure.distance.Distance;
import org.mwg.structure.distance.Distances;
import org.mwg.structure.util.VolatileResult;

public class ETree extends BaseNode implements Tree {

    public static String NAME = "ETree";

    public static long BOUND_MIN = 0;
    public static long BOUND_MAX = 1;
    public static long RESOLUTION = 2;
    public static long BUFFER_SIZE = 3;
    public static long DISTANCE = 4;
    public static long DISTANCE_THRESHOLD = 5;
    private static long EGRAPH = 6;
    private static long STRATEGY = 7;

    public static int BUFFER_SIZE_DEF = 20;
    public static int DISTANCE_DEF = Distances.DEFAULT;
    public static double DISTANCE_THRESHOLD_DEF = 1e-20;


    private static long _TOTAL = 0;
    private static long _SUBNODES = 1;
    private static long _TOTAL_SUBNODES = 2;
    private static long _MIN = 3;
    private static long _MAX = 4;
    private static long _BUFFER_KEYS = 5;
    private static long _BUFFER_VALUES = 6;
    private static long _VALUE = 7;
    private static long _PROFILE = 8;

    private static int _REL = 16;

    public ETree(long p_world, long p_time, long p_id, Graph p_graph) {
        super(p_world, p_time, p_id, p_graph);
    }

    private static int getRelationId(double[] centerKey, double[] keyToInsert) {
        int result = 0;
        for (int i = 0; i < centerKey.length; i++) {
            if (i != 0) {
                result = result << 1;
            }
            if (keyToInsert[i] > centerKey[i]) {
                result += 1;
            }
        }
        return result + _REL;
    }

    private static boolean checkCreateLevels(double[] min, double[] max, double[] resolutions) {
        for (int i = 0; i < min.length; i++) {
            //todo optimize the 2* later
            if ((max[i] - min[i]) > 2 * resolutions[i]) {
                return true;
            }
        }
        return false;
    }

    private static double[] getCenterMinMax(double[] min, double[] max) {
        double[] center = new double[min.length];
        for (int i = 0; i < min.length; i++) {
            center[i] = (max[i] + min[i]) / 2;
        }
        return center;
    }

    private static double[] getCenter(ENode node) {
        double[] min = (double[]) node.getAt(_MIN);
        double[] max = (double[]) node.getAt(_MAX);
        return getCenterMinMax(min, max);
    }


    private static void check(double[] values, double[] min, double[] max) {
        if (min == null || max == null) {
            throw new RuntimeException("Please set min and max boundary before inserting in the tree");
        }

        if (values.length != min.length) {
            throw new RuntimeException("Values dimension mismatch");
        }
        for (int i = 0; i < min.length; i++) {
            if (values[i] < min[i] || values[i] > max[i]) {
                throw new RuntimeException("Values should be between min, max!");
            }
        }
    }


    private static ENode createNewNode(final ENode parent, final ENode root, int index, final double[] min, final double[] max, final double[] center, final double[] keyToInsert, final int buffersize) {
        ENode node = parent.graph().newNode();
        double[] minChild = new double[min.length];
        double[] maxChild = new double[max.length];

        for (int i = 0; i < min.length; i++) {
            if (keyToInsert[i] <= center[i]) {
                minChild[i] = min[i];
                maxChild[i] = center[i];

            } else {
                minChild[i] = center[i];
                maxChild[i] = max[i];
            }
        }
        node.setAt(_SUBNODES, Type.INT, 0);
        node.setAt(_MIN, Type.DOUBLE_ARRAY, minChild);
        node.setAt(_MAX, Type.DOUBLE_ARRAY, maxChild);
        node.setAt(_TOTAL, Type.LONG, 0);


        root.setAt(_TOTAL_SUBNODES, Type.INT, (int) root.getAt(_TOTAL_SUBNODES) + 1);
        parent.setAt(_SUBNODES, Type.INT, (int) parent.getAt(_SUBNODES) + 1);
        parent.setAt(index, Type.ENODE, node);

        //todo create buffer here
       /* if (buffersize != 0) {

        }*/
        return node;
    }

    private static boolean subInsert(final ENode parent, final double[] key, final long value, final int strategyType, final double[] min, final double[] max, final double[] center, final double[] resolution, final int buffersize, final ENode root, boolean bufferupdate) {
        int index = getRelationId(center, key);

        ENode child = (ENode) parent.getAt(index);
        if (child == null) {
            child = createNewNode(parent, root, index, min, max, center, key, buffersize);
        }
        double[] childmin = (double[]) child.getAt(_MIN);
        double[] childmax = (double[]) child.getAt(_MAX);
        double[] childcenter = getCenterMinMax(childmin, childmax);
        boolean res = internalInsert(child, key, value, strategyType, childmin, childmax, childcenter, resolution, buffersize, root);
        res = res && !bufferupdate;

        if (res) {
            switch (strategyType) {
                case IndexStrategy.PROFILE: {
                    parent.setAt(_TOTAL, Type.LONG, (int) parent.getAt(_TOTAL) + (int) value);
                    break;
                }
                case IndexStrategy.INDEX: {
                    parent.setAt(_TOTAL, Type.LONG, (int) parent.getAt(_TOTAL) + 1);
                    break;
                }
                default: {
                    throw new RuntimeException("Index strategy is wrong!");
                }
            }
        }
        return res;
    }

    private static boolean internalInsert(final ENode node, final double[] key, final long value, final int strategyType, final double[] min, final double[] max, final double[] center, final double[] resolution, final int buffersize, final ENode root) {
        if ((int) node.getAt(_SUBNODES) != 0) {
            return subInsert(node, key, value, strategyType, min, max, center, resolution, buffersize, root, false);
        } else if (checkCreateLevels(min, max, resolution)) {
            DMatrix buffer = null;
            if (buffersize > 0) {
                buffer = (DMatrix) node.getOrCreateAt(_BUFFER_KEYS, Type.DMATRIX);
            }
            if (buffer != null) {
                //First step check if it already exists in the buffer
                for (int i = 0; i < buffer.columns(); i++) {
                    if (compare(key, buffer.column(i), resolution)) {
                        switch (strategyType) {
                            case IndexStrategy.PROFILE: {
                                DMatrix bufferkeys = (DMatrix) node.getAt(_PROFILE);
                                for (int j = 0; j < key.length; j++) {
                                    bufferkeys.set(j, i, bufferkeys.get(j, i) + key[j] * value);
                                }
                                LMatrix bufferValue = (LMatrix) node.getAt(_BUFFER_VALUES);
                                bufferValue.set(0, i, bufferValue.get(0, i) + value);
                                node.setAt(_TOTAL, Type.LONG, (long) node.getAt(_TOTAL) + value);
                                return true; //to update parent total
                            }
                            case IndexStrategy.INDEX: {
                                LMatrix bufferValue = (LMatrix) node.getAt(_BUFFER_VALUES);
                                bufferValue.set(0, i, (long) value);
                                return false; //Should not update parent total
                            }
                            default: {
                                throw new RuntimeException("Index strategy is wrong!");
                            }
                        }
                    }
                }
                //Here it is not in the buffer, we check if we can append
                if (buffer.columns() < buffersize) {
                    buffer.appendColumn(key);
                    switch (strategyType) {
                        case IndexStrategy.PROFILE: {
                            DMatrix bufferkeys = (DMatrix) node.getOrCreateAt(_PROFILE, Type.DMATRIX);
                            bufferkeys.appendColumn(key);
                            LMatrix bufferValue = (LMatrix) node.getOrCreateAt(_BUFFER_VALUES, Type.LMATRIX);
                            bufferValue.appendColumn(new long[]{(int) value});
                            node.setAt(_TOTAL, Type.LONG, (long) node.getAt(_TOTAL) + value);
                            return true; //to update parent total
                        }

                        case IndexStrategy.INDEX: {
                            LMatrix bufferValue = (LMatrix) node.getOrCreateAt(_BUFFER_VALUES, Type.LMATRIX);
                            bufferValue.appendColumn(new long[]{(long) value});
                            node.setAt(_TOTAL, Type.LONG, (long) node.getAt(_TOTAL) + 1);
                            return true; //to update parent total
                        }
                        default: {
                            throw new RuntimeException("Index strategy is wrong!");
                        }
                    }
                }
                //here buffer is full we need to reinsert
                else {
                    //if it is a profile, get the average of all the keys and update the buffer before reinserting
                    if (strategyType == IndexStrategy.PROFILE) {
                        DMatrix bufferkeys = (DMatrix) node.getAt(_PROFILE);
                        LMatrix bufferValue = (LMatrix) node.getAt(_BUFFER_VALUES);
                        for (int i = 0; i < buffer.columns(); i++) {
                            int t = (int) bufferValue.get(0, i);
                            for (int j = 0; j < buffer.rows(); j++) {
                                buffer.set(j, i, bufferkeys.get(j, i) / t);
                            }
                        }
                        node.setAt(_PROFILE, Type.DMATRIX, null);
                    }


                    //reinsert all children

                    LMatrix bufferValue = (LMatrix) node.getAt(_BUFFER_VALUES);
                    for (int i = 0; i < buffer.columns(); i++) {
                        subInsert(node, buffer.column(i), bufferValue.get(0, i), strategyType, min, max, center, resolution, buffersize, root, true);
                    }
                    node.setAt(_BUFFER_VALUES, Type.LMATRIX, null);

                    //clear the buffer, update the total, and insert the new value
                    node.setAt(_BUFFER_KEYS, Type.DMATRIX, null);
                    return subInsert(node, key, value, strategyType, min, max, center, resolution, buffersize, root, false);

                }


            } //null buffer means to subinsert as long as we can create levels
            else {
                return subInsert(node, key, value, strategyType, min, max, center, resolution, buffersize, root, false);
            }
        }
        //Else we reached here last level of the tree, and the array is full, we need to start a profiler
        else {
            switch (strategyType) {
                case IndexStrategy.PROFILE: {
                    //todo add the value later
                    double[] profile = (double[]) node.getAt(_PROFILE);
                    if (profile == null) {
                        profile = new double[key.length];
                        System.arraycopy(key, 0, profile, 0, key.length);
                    } else {
                        for (int i = 0; i < key.length; i++) {
                            profile[i] += key[i] * value;
                        }
                    }
                    node.setAt(_PROFILE, Type.DOUBLE_ARRAY, profile);
                    node.setAt(_TOTAL, Type.LONG, (long) node.getAt(_TOTAL) + value);
                    return true; //to update parent total
                }
                case IndexStrategy.INDEX: {
                    if ((int) node.getAt(_TOTAL) == 0) {
                        node.setAt(_PROFILE, Type.DOUBLE_ARRAY, key);
                        node.setAt(_VALUE, Type.LONG, value);
                        node.setAt(_TOTAL, Type.LONG, 1);
                        return true;
                    } else {
                        node.setAt(_PROFILE, Type.DOUBLE_ARRAY, key);
                        node.setAt(_VALUE, Type.LONG, value);
                        return false;
                    }
                }
                default: {
                    throw new RuntimeException("Index strategy is wrong!");
                }
            }

        }

    }

    private static boolean compare(double[] key1, double[] key2, double[] precision) {
        //todo compare with distance
        for (int i = 0; i < key1.length; i++) {
            if (Math.abs(key1[i] - key2[i]) > precision[i]) {
                return false;
            }
        }
        return true;
    }


    @Override
    public void setDistance(int distanceType) {
        super.setAt(DISTANCE, Type.INT, distanceType);
    }

    @Override
    public void setDistanceThreshold(double distanceThreshold) {
        super.setAt(DISTANCE_THRESHOLD, Type.DOUBLE, distanceThreshold);
    }


    @Override
    public void setStrategy(byte strategy) {

    }

    @Override
    public void insert(final double[] keys, final long value) {
        NodeState state = unphasedState();

        double[] min = (double[]) state.get(BOUND_MIN);
        double[] max = (double[]) state.get(BOUND_MAX);
        check(keys, min, max);

        double[] resolution = (double[]) state.get(RESOLUTION);
        EGraph graph = (EGraph) state.getOrCreate(EGRAPH, Type.EGRAPH);
        int buffersize = state.getWithDefault(BUFFER_SIZE, BUFFER_SIZE_DEF);
        Distance distance = Distances.getDistance(state.getWithDefault(DISTANCE, DISTANCE_DEF));


        ENode root = graph.root();
        if (root == null) {
            root = graph.newNode();
            state.set(STRATEGY, Type.INT, IndexStrategy.INDEX);
            graph.setRoot(root);
            root.setAt(_TOTAL, Type.LONG, 0);
            root.setAt(_SUBNODES, Type.INT, 0);
            root.setAt(_MIN, Type.DOUBLE_ARRAY, min);
            root.setAt(_MAX, Type.DOUBLE_ARRAY, max);
        }
        internalInsert(root, keys, value, IndexStrategy.INDEX, min, max, getCenterMinMax(min, max), resolution, buffersize, root);

    }

    @Override
    public void profile(final double[] keys, final long occurrence) {
        NodeState state = unphasedState();

        double[] min = (double[]) state.get(BOUND_MIN);
        double[] max = (double[]) state.get(BOUND_MAX);
        check(keys, min, max);

        double[] resolution = (double[]) state.get(RESOLUTION);
        EGraph graph = (EGraph) state.getOrCreate(EGRAPH, Type.EGRAPH);
        int buffersize = state.getWithDefault(BUFFER_SIZE, BUFFER_SIZE_DEF);
        Distance distance = Distances.getDistance(state.getWithDefault(DISTANCE, DISTANCE_DEF));


        ENode root = graph.root();
        if (root == null) {
            root = graph.newNode();
            state.set(STRATEGY, Type.INT, IndexStrategy.PROFILE);
            graph.setRoot(root);
            root.setAt(_TOTAL, Type.LONG, 0);
            root.setAt(_TOTAL_SUBNODES, Type.INT, 0);
            root.setAt(_SUBNODES, Type.INT, 0);
            root.setAt(_MIN, Type.DOUBLE_ARRAY, min);
            root.setAt(_MAX, Type.DOUBLE_ARRAY, max);
        }
        internalInsert(root, keys, occurrence, IndexStrategy.PROFILE, min, max, getCenterMinMax(min, max), resolution, buffersize, root);

    }

    @Override
    public TreeResult nearestN(double[] keys, int nbElem) {
        NodeState state = unphasedState();

        double[] min = (double[]) state.get(BOUND_MIN);
        double[] max = (double[]) state.get(BOUND_MAX);
        check(keys, min, max);

        EGraph graph = (EGraph) state.getOrCreate(EGRAPH, Type.EGRAPH);
        Distance distance = Distances.getDistance(state.getWithDefault(DISTANCE, DISTANCE_DEF));
        int strategyType = (int) state.get(STRATEGY);


        ENode root = graph.root();
        if (root == null) {
            return null;
        }

        EGraph calcZone = graph().space().newVolatileGraph();
        if (nbElem <= 0) {
            throw new RuntimeException("nb elements can't be <=0");
        }
        VolatileResult nnl = new VolatileResult(calcZone.newNode(), nbElem);
        reccursiveTraverse(root, calcZone, nnl, strategyType, distance, keys, null, null, null, -1);

        return nnl;
    }

    @Override
    public TreeResult nearestWithinRadius(double[] keys, double radius) {

        NodeState state = unphasedState();

        double[] min = (double[]) state.get(BOUND_MIN);
        double[] max = (double[]) state.get(BOUND_MAX);
        check(keys, min, max);

        EGraph graph = (EGraph) state.getOrCreate(EGRAPH, Type.EGRAPH);
        Distance distance = Distances.getDistance(state.getWithDefault(DISTANCE, DISTANCE_DEF));
        int strategyType = (int) state.get(STRATEGY);


        ENode root = graph.root();
        if (root == null) {
            return null;
        }

        EGraph calcZone = graph().space().newVolatileGraph();
        VolatileResult nnl = new VolatileResult(calcZone.newNode(), -1);
        reccursiveTraverse(root, calcZone, nnl, strategyType, distance, keys, null, null, null, radius);

        return nnl;
    }

    @Override
    public TreeResult nearestNWithinRadius(double[] keys, int nbElem, double radius) {
        NodeState state = unphasedState();

        double[] min = (double[]) state.get(BOUND_MIN);
        double[] max = (double[]) state.get(BOUND_MAX);
        check(keys, min, max);

        EGraph graph = (EGraph) state.getOrCreate(EGRAPH, Type.EGRAPH);
        Distance distance = Distances.getDistance(state.getWithDefault(DISTANCE, DISTANCE_DEF));
        int strategyType = (int) state.get(STRATEGY);


        ENode root = graph.root();
        if (root == null) {
            return null;
        }

        EGraph calcZone = graph().space().newVolatileGraph();
        if (nbElem <= 0) {
            throw new RuntimeException("nb elements can't be <=0");
        }
        VolatileResult nnl = new VolatileResult(calcZone.newNode(), nbElem);
        reccursiveTraverse(root, calcZone, nnl, strategyType, distance, keys, null, null, null, radius);

        return nnl;

    }


    @Override
    public TreeResult query(double[] min, double[] max) {
        NodeState state = unphasedState();

        EGraph graph = (EGraph) state.getOrCreate(EGRAPH, Type.EGRAPH);
        Distance distance = Distances.getDistance(state.getWithDefault(DISTANCE, DISTANCE_DEF));
        int strategyType = (int) state.get(STRATEGY);

        final double[] center = new double[max.length];

        for(int i=0;i<center.length;i++){
            center[i]=(min[i]+max[i])/2;
        }

        ENode root = graph.root();
        if (root == null) {
            return null;
        }

        EGraph calcZone = graph().space().newVolatileGraph();
        VolatileResult nnl = new VolatileResult(calcZone.newNode(), -1);
        reccursiveTraverse(root, calcZone, nnl, strategyType, distance, null, min, max, center, -1);

        return nnl;
    }


    @Override
    public int size() {
        NodeState state = unphasedState();
        EGraph graph = (EGraph) state.getOrCreate(EGRAPH, Type.EGRAPH);
        ENode root = graph.root();
        if (root == null) {
            return 0;
        } else {
            return (int) root.getAt(_TOTAL);
        }
    }

    @Override
    public int numberOfNodes() {
        NodeState state = unphasedState();
        EGraph graph = (EGraph) state.getOrCreate(EGRAPH, Type.EGRAPH);
        ENode root = graph.root();
        if (root == null) {
            return 0;
        } else {
            return (int) root.getAt(_TOTAL_SUBNODES);
        }
    }


    private static boolean[] binaryFromLong(long value, int dim) {
        long tempvalue = value - _REL;
        long shiftvalue = tempvalue >> 1;
        boolean[] res = new boolean[dim];
        for (int i = 0; i < dim; i++) {
            res[dim - i - 1] = ((tempvalue - (shiftvalue << 1)) == 1);
            tempvalue = shiftvalue;
            shiftvalue = tempvalue >> 1;
        }
        return res;
    }

    private static boolean checkInside(final double[] targetmin, final double[] targetmax, final double[] boundMin, final double[] boundMax) {
        for (int i = 0; i < boundMax.length; i++) {
            if (targetmin[i] > boundMax[i] || targetmax[i] < boundMin[i]) {
                return false;
            }
        }
        return true;
    }


    private static boolean checkInsideBounds(final double[] key, final double[] boundMin, final double[] boundMax) {
        for (int i = 0; i < boundMax.length; i++) {
            if (key[i] > boundMax[i] || key[i] < boundMin[i]) {
                return false;
            }
        }
        return true;
    }


    private static void reccursiveTraverse(final ENode node, final EGraph calcZone, final VolatileResult nnl, final int strategyType, final Distance distance, final double[] target, final double[] targetmin, final double[] targetmax, final double[] targetcenter, final double radius) {

        if ((int) node.getAt(_SUBNODES) == 0) {
            //Leave node
            DMatrix buffer = (DMatrix) node.getAt(_BUFFER_KEYS);
            LMatrix bufferValue = (LMatrix) node.getAt(_BUFFER_VALUES);

            if (buffer != null) {
                //Bufferizing node
                switch (strategyType) {
                    case IndexStrategy.PROFILE: {
                        double[] tempK = new double[target.length];

                        DMatrix bufferkeys = (DMatrix) node.getAt(_PROFILE);
                        for (int i = 0; i < buffer.columns(); i++) {
                            long t = bufferValue.get(0, i);
                            for (int j = 0; j < buffer.rows(); j++) {
                                tempK[j] = bufferkeys.get(j, i) / t;
                            }
                            checkAndInsert(tempK, t, target, targetmin, targetmax, targetcenter, distance, radius, nnl);
                        }
                        return;
                    }
                    case IndexStrategy.INDEX: {
                        for (int i = 0; i < buffer.columns(); i++) {
                            checkAndInsert(buffer.column(i), bufferValue.get(0, i), target, targetmin, targetmax, targetcenter, distance, radius, nnl);
                        }
                        return;

                    }
                    default: {
                        throw new RuntimeException("Index strategy is wrong!");
                    }
                }

            } else {
                //Very End node
                switch (strategyType) {
                    case IndexStrategy.PROFILE: {
                        double[] keyo = (double[]) node.getAt(_PROFILE);
                        double[] key = new double[keyo.length];
                        long value = (long) node.getAt(_TOTAL);
                        for (int i = 0; i < keyo.length; i++) {
                            key[i] = keyo[i] / value;
                        }
                        checkAndInsert(key, value, target, targetmin, targetmax, targetcenter, distance, radius, nnl);
                        return;
                    }
                    case IndexStrategy.INDEX: {
                        double[] key = (double[]) node.getAt(_PROFILE);
                        long value = (long) node.getAt(_VALUE);
                        checkAndInsert(key, value, target, targetmin, targetmax, targetcenter, distance, radius, nnl);
                        return;
                    }
                    default: {
                        throw new RuntimeException("Index strategy is wrong!");
                    }
                }
            }

        } else {
            //Parent node
            final double[] boundMax = (double[]) node.getAt(BOUND_MAX);
            final double[] boundMin = (double[]) node.getAt(BOUND_MIN);
            final double worst = nnl.getWorstDistance();

            if (targetmin == null || targetmax == null) {
                if (!nnl.isCapacityReached() || getclosestDistance(target, boundMin, boundMax, distance) <= worst) {
                    final ENode tempList = calcZone.newNode();
                    final VolatileResult childPriority = new VolatileResult(tempList, -1);
                    final int dim = boundMax.length;
                    final double[] childMin = new double[dim];
                    final double[] childMax = new double[dim];

                    node.each(new NodeStateCallback() {
                        @Override
                        public void on(long attributeKey, byte elemType, Object elem) {
                            if (attributeKey >= _REL) {
                                boolean[] binaries = binaryFromLong(attributeKey, dim);
                                for (int i = 0; i < dim; i++) {
                                    if (!binaries[i]) {
                                        childMin[i] = boundMin[i];
                                        childMax[i] = (boundMax[i] + boundMin[i]) / 2;

                                    } else {
                                        childMin[i] = (boundMax[i] + boundMin[i]) / 2;
                                        childMax[i] = boundMax[i];
                                    }
                                }
                                childPriority.insert(childMin, attributeKey, getclosestDistance(target, boundMin, boundMax, distance));
                            }
                        }


                    });
                    childPriority.sort(true);
                    for (int i = 0; i < childPriority.size(); i++) {
                        ENode child = (ENode) node.getAt(childPriority.value(i));
                        reccursiveTraverse(child, calcZone, nnl, strategyType, distance, target, targetmin, targetmax, targetcenter, radius);
                    }
                    childPriority.free();
                }
            } else {
                node.each(new NodeStateCallback() {
                    @Override
                    public void on(long attributeKey, byte elemType, Object elem) {
                        if (attributeKey >= _REL) {
                            ENode child = (ENode) node.getAt(attributeKey);
                            if (checkInside(targetmin, targetmax, (double[]) child.getAt(BOUND_MIN), (double[]) child.getAt(BOUND_MIN))) {
                                reccursiveTraverse(child, calcZone, nnl, strategyType, distance, target, targetmin, targetmax, targetcenter, radius);
                            }
                        }
                    }
                });
            }
        }
    }

    private static double getclosestDistance(double[] target, double[] boundMin, double[] boundMax, Distance distance) {
        double[] closest = new double[target.length];

        for (int i = 0; i < target.length; i++) {
            if (target[i] >= boundMax[i]) {
                closest[i] = boundMax[i];
            } else if (target[i] <= boundMin[i]) {
                closest[i] = boundMin[i];
            } else {
                closest[i] = target[i];
            }
        }
        return distance.measure(closest, target);
    }

    private static void checkAndInsert(double[] key, long value, double[] target, double[] targetmin, double[] targetmax, double[] targetcenter, Distance distance, double radius, VolatileResult nnl) {
        if (targetmin != null) {
            if (checkInsideBounds(key, targetmin, targetmax)) {
                nnl.insert(key, value, distance.measure(key, targetcenter));
            }
        } else {
            double dist = distance.measure(key, target);
            if (radius > 0) {
                if (dist < radius) {
                    nnl.insert(key, value, dist);
                }
            } else {
                nnl.insert(key, value, dist);
            }
        }

    }

}
