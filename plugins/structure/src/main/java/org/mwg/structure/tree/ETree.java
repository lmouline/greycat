package org.mwg.structure.tree;

import org.mwg.Callback;
import org.mwg.Graph;
import org.mwg.Type;
import org.mwg.base.BaseNode;
import org.mwg.plugin.NodeState;
import org.mwg.struct.EGraph;
import org.mwg.struct.ENode;
import org.mwg.struct.Matrix;
import org.mwg.structure.IndexStrategy;
import org.mwg.structure.Tree;
import org.mwg.structure.distance.Distance;
import org.mwg.structure.distance.Distances;

public class ETree extends BaseNode implements Tree {

    public static String NAME = "ETree";

    public static long BOUND_MIN = 0;
    public static long BOUND_MAX = 1;
    public static long RESOLUTION = 2;
    public static long BUFFER_SIZE = 3;
    public static long STRATEGY_TYPE = 4;
    public static long DISTANCE = 5;
    public static long DISTANCE_THRESHOLD = 6;
    private static long EGRAPH = 7;

    public static int BUFFER_SIZE_DEF = 20;
    public static Byte STRATEGY_DEF = IndexStrategy.DEFAULT;
    public static int DISTANCE_DEF = Distances.DEFAULT;
    public static double DISTANCE_THRESHOLD_DEF = 1e-20;


    private static long _TOTAL = 0;
    private static long _BUFFER_KEYS = 1;
    private static long _BUFFER_VALUES = 2;
    private static long _SUBNODES = 3;
    private static long _VALUE = 4;
    private static long _MIN = 5;
    private static long _MAX = 6;
    private static long _PROFILE = 7;
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
        if (values.length != min.length) {
            throw new RuntimeException("Values dimension mismatch");
        }
        for (int i = 0; i < min.length; i++) {
            if (values[i] < min[i] || values[i] > max[i]) {
                throw new RuntimeException("Values should be between min, max!");
            }
        }
    }


    private static ENode createNewNode(final ENode parent, int index, final double[] min, final double[] max, final double[] center, final double[] keyToInsert, final int buffersize) {
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
        node.setAt(_TOTAL, Type.INT, 0);

        parent.setAt(_SUBNODES, Type.INT, (int) parent.getAt(_SUBNODES) + 1);
        parent.setAt(index, Type.LONG, node.id());

        if (buffersize != 0) {
            //todo create buffer here
        }
        return node;
    }

    private static void subInsert(final ENode from, final double[] keys, final Type valuetype, final Object value, final byte strategyType, final double[] min, final double[] max, final double[] center, final double[] resolution, final int buffersize, final ENode root) {
        int index = getRelationId(center, keys);

        ENode child;
        if (from.getAt(index) == null) {
            child = createNewNode(from, index, min, max, center, keys, buffersize);

        } else {
            child = from.graph().lookup((long) from.getAt(index));
        }
        double[] childmin = (double[]) child.getAt(_MIN);
        double[] childmax = (double[]) child.getAt(_MAX);
        double[] childcenter = getCenterMinMax(childmin, childmax);
        internalInsert(child, keys, valuetype, value, strategyType, childmin, childmax, childcenter, resolution, buffersize, root);
    }

    private static void internalInsert(final ENode node, final double[] keys, final Type valuetype, final Object value, final byte strategyType, final double[] min, final double[] max, final double[] center, final double[] resolution, final int buffersize, final ENode root) {
        if ((int) node.getAt(_SUBNODES) != 0) {
            subInsert(node, keys, valuetype, value, strategyType, min, max, center, resolution, buffersize, root);
        } else if (checkCreateLevels(min, max, resolution)) {

            Matrix buffer = (Matrix) node.getAt(_BUFFER_KEYS);
            if (buffer != null) {
//                for (int i = 0; i < buffer.columns(); i++) {
//                    if (compare(keys, buffer.column(i))) {
//
//                    }
//
//                }
//
//                if (buffer.columns() < buffersize) {
//
//                }

//                if (_tempValues.size() < buffersize) {
//                    _tempValues.add(keys);
//                } else {
//                    // check if we can create subchildren
//                    _subchildren = new NDTree[getChildren(min.length)];
//                    for (double[] _tempValue : _tempValues) {
//                        subInsert(this, _tempValue, min, max, center, resolution, maxPerLevel, lev, root);
//                    }
//                    subInsert(this, keys, min, max, center, resolution, maxPerLevel, lev, root);
//                    _tempValues = null;
//                }
            } else {
                subInsert(node, keys, valuetype, value, strategyType, min, max, center, resolution, buffersize, root);
            }
        }
        //Else we reached here last level of the tree, and the array is full, we need to start a profiler
        else {
            //todo add the value later
            double[] profile = (double[]) node.getAt(_PROFILE);
            if (profile == null) {
                profile = new double[keys.length];
                System.arraycopy(keys, 0, profile, 0, keys.length);
            } else {
                for (int i = 0; i < keys.length; i++) {
                    profile[i] += keys[i];
                }
            }
            node.setAt(_PROFILE, Type.DOUBLE_ARRAY, profile);
        }

        //this is for everyone
        node.setAt(_TOTAL, Type.INT, (int) node.getAt(_TOTAL) + 1);
    }

    private static boolean compare(double[] keys, double[] column) {
        return false;
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
    public void insertWith(double[] keys, final Type valuetype, final Object value, Callback<Boolean> callback) {
        NodeState state = unphasedState();

        double[] min = (double[]) state.get(BOUND_MIN);
        double[] max = (double[]) state.get(BOUND_MAX);
        check(keys, min, max);

        double[] resolution = (double[]) state.get(RESOLUTION);
        EGraph graph = (EGraph) state.getOrCreate(EGRAPH, Type.EGRAPH);
        byte strategyType = state.getWithDefault(STRATEGY_TYPE, STRATEGY_DEF);
        int buffersize = state.getWithDefault(BUFFER_SIZE, BUFFER_SIZE_DEF);
        Distance distance = Distances.getDistance(state.getWithDefault(DISTANCE, DISTANCE_DEF));


        ENode root = graph.root();
        if (root == null) {
            root = graph.newNode();
            graph.setRoot(root);
            root.setAt(_TOTAL, Type.INT, 0);
            root.setAt(_SUBNODES, Type.INT, 0);
            root.setAt(_MIN, Type.DOUBLE_ARRAY, min);
            root.setAt(_MAX, Type.DOUBLE_ARRAY, max);
        }
        internalInsert(root, keys, valuetype, value, strategyType, min, max, getCenterMinMax(min, max), resolution, buffersize, root);
        if (callback != null) {
            callback.on(true);
        }
    }


    @Override
    public void nearestN(double[] keys, int nbElem, Callback<Object[]> callback) {

    }

    @Override
    public void nearestWithinRadius(double[] keys, double radius, Callback<Object[]> callback) {

    }

    @Override
    public void nearestNWithinRadius(double[] keys, int nbElem, double radius, Callback<Object[]> callback) {

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


}
