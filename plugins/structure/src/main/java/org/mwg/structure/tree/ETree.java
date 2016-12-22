package org.mwg.structure.tree;

import org.mwg.Graph;
import org.mwg.Type;
import org.mwg.base.BaseNode;
import org.mwg.plugin.NodeState;
import org.mwg.struct.EGraph;
import org.mwg.struct.ENode;
import org.mwg.struct.LMatrix;
import org.mwg.struct.Matrix;
import org.mwg.structure.Tree;
import org.mwg.structure.distance.Distance;
import org.mwg.structure.distance.Distances;

public class ETree extends BaseNode implements Tree {

    public static String NAME = "ETree";

    public static long BOUND_MIN = 0;
    public static long BOUND_MAX = 1;
    public static long RESOLUTION = 2;
    public static long BUFFER_SIZE = 3;
    public static long DISTANCE = 4;
    public static long DISTANCE_THRESHOLD = 5;
    private static long EGRAPH = 6;

    public static int BUFFER_SIZE_DEF = 20;
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
        parent.setAt(index, Type.ENODE, node);

        if (buffersize != 0) {
            //todo create buffer here
        }
        return node;
    }

    private static boolean subInsert(final ENode parent, final double[] key, final byte valuetype, final Object value, final int strategyType, final double[] min, final double[] max, final double[] center, final double[] resolution, final int buffersize, final ENode root, boolean bufferupdate) {
        int index = getRelationId(center, key);

        ENode child = (ENode) parent.getAt(index);
        if (child == null) {
            child = createNewNode(parent, index, min, max, center, key, buffersize);
        }
        double[] childmin = (double[]) child.getAt(_MIN);
        double[] childmax = (double[]) child.getAt(_MAX);
        double[] childcenter = getCenterMinMax(childmin, childmax);
        boolean res = internalInsert(child, key, valuetype, value, strategyType, childmin, childmax, childcenter, resolution, buffersize, root);
        res = res && bufferupdate;

        if (res) {
            switch (strategyType) {
                case IndexStrategy.PROFILE: {
                    parent.setAt(_TOTAL, Type.INT, (int) parent.getAt(_TOTAL) + (int) value);
                    break;
                }
                case IndexStrategy.REPLACE: {
                    parent.setAt(_TOTAL, Type.INT, (int) parent.getAt(_TOTAL) + 1);
                    break;
                }
                default: {
                    throw new RuntimeException("Index strategy is wrong!");
                }
            }
        }
        return res;
    }

    private static boolean internalInsert(final ENode node, final double[] key, final byte valuetype, final Object value, final int strategyType, final double[] min, final double[] max, final double[] center, final double[] resolution, final int buffersize, final ENode root) {
        if ((int) node.getAt(_SUBNODES) != 0) {
            return subInsert(node, key, valuetype, value, strategyType, min, max, center, resolution, buffersize, root, false);
        } else if (checkCreateLevels(min, max, resolution)) {
            Matrix buffer=null;
            if (buffersize > 0) {
                buffer = (Matrix) node.getOrCreateAt(_BUFFER_KEYS, Type.MATRIX);
            }
            if (buffer != null) {
                //First step check if it already exists in the buffer
                for (int i = 0; i < buffer.columns(); i++) {
                    if (compare(key, buffer.column(i))) {
                        switch (strategyType) {
                            case IndexStrategy.PROFILE: {
                                LMatrix bufferValue = (LMatrix) node.getAt(_BUFFER_VALUES);
                                bufferValue.set(0, i, bufferValue.get(0, i) + (int) value);
                                node.setAt(_TOTAL, Type.INT, (int) node.getAt(_TOTAL) + (int) value);
                                return true; //to update parent total
                            }
                            case IndexStrategy.REPLACE: {
                                if (valuetype == Type.LONG || valuetype == Type.INT) {
                                    LMatrix bufferValue = (LMatrix) node.getAt(_BUFFER_VALUES);
                                    bufferValue.set(0, i, (long) value);
                                } else if (valuetype == Type.DOUBLE) {
                                    Matrix bufferValue = (Matrix) node.getAt(_BUFFER_VALUES);
                                    bufferValue.set(0, i, (double) value);
                                }
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
                            LMatrix bufferValue = (LMatrix) node.getOrCreateAt(_BUFFER_VALUES,Type.LMATRIX);
                            bufferValue.appendColumn(new long[]{(int) value});
                            node.setAt(_TOTAL, Type.INT, (int) node.getAt(_TOTAL) + (int) value);
                            return true; //to update parent total
                        }
                        case IndexStrategy.REPLACE: {
                            if (valuetype == Type.LONG || valuetype == Type.INT) {
                                LMatrix bufferValue = (LMatrix) node.getOrCreateAt(_BUFFER_VALUES,Type.LMATRIX);
                                bufferValue.appendColumn(new long[]{(long) value});
                            } else if (valuetype == Type.DOUBLE) {
                                Matrix bufferValue = (Matrix) node.getOrCreateAt(_BUFFER_VALUES,Type.MATRIX);
                                bufferValue.appendColumn(new double[]{(double) value});
                            }
                            node.setAt(_TOTAL, Type.INT, (int) node.getAt(_TOTAL) + 1);
                            return true; //to update parent total
                        }
                        default: {
                            throw new RuntimeException("Index strategy is wrong!");
                        }
                    }
                }
                //here buffer is full we need to reinsert
                else {
                    //reinsert all children

                    if (valuetype == Type.LONG) {
                        LMatrix bufferValue = (LMatrix) node.getAt(_BUFFER_VALUES);
                        for (int i = 0; i < buffer.columns(); i++) {
                            subInsert(node, buffer.column(i), valuetype, bufferValue.get(0, i), strategyType, min, max, center, resolution, buffersize, root, true);
                        }
                        node.setAt(_BUFFER_VALUES, Type.LMATRIX, null);
                    } else if (valuetype == Type.INT) {
                        LMatrix bufferValue = (LMatrix) node.getAt(_BUFFER_VALUES);
                        for (int i = 0; i < buffer.columns(); i++) {
                            subInsert(node, buffer.column(i), valuetype, (int) bufferValue.get(0, i), strategyType, min, max, center, resolution, buffersize, root, true);
                        }
                        node.setAt(_BUFFER_VALUES, Type.LMATRIX, null);
                    } else if (valuetype == Type.DOUBLE) {
                        Matrix bufferValue = (Matrix) node.getAt(_BUFFER_VALUES);
                        for (int i = 0; i < buffer.columns(); i++) {
                            subInsert(node, buffer.column(i), valuetype, bufferValue.get(0, i), strategyType, min, max, center, resolution, buffersize, root, true);
                        }
                        node.setAt(_BUFFER_VALUES, Type.MATRIX, null);
                    }

                    //clear the buffer, update the total, and insert the new value
                    node.setAt(_BUFFER_KEYS, Type.MATRIX, null);
                    return subInsert(node, key, valuetype, value, strategyType, min, max, center, resolution, buffersize, root, false);
                }


            } //null buffer means to subinsert as long as we can create levels
            else {
                return subInsert(node, key, valuetype, value, strategyType, min, max, center, resolution, buffersize, root, false);
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
                            profile[i] += key[i];
                        }
                    }
                    node.setAt(_PROFILE, Type.DOUBLE_ARRAY, profile);
                    node.setAt(_TOTAL, Type.INT, (int) node.getAt(_TOTAL) + (int) value);
                    return true; //to update parent total
                }
                case IndexStrategy.REPLACE: {
                    if ((int) node.getAt(_TOTAL) == 0) {
                        node.setAt(_PROFILE, Type.DOUBLE_ARRAY, key);
                        node.setAt(_VALUE, valuetype, value);
                        node.setAt(_TOTAL, Type.INT, 1);
                        return true;
                    } else {
                        node.setAt(_PROFILE, Type.DOUBLE_ARRAY, key);
                        node.setAt(_VALUE, valuetype, value);
                        return false;
                    }
                }
                default: {
                    throw new RuntimeException("Index strategy is wrong!");
                }
            }

        }

    }

    private static boolean compare(double[] keys, double[] column) {
        //todo compare with distance
        for (int i = 0; i < keys.length; i++) {
            if (keys[i] != column[i]) {
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
    public void insertWith(double[] keys, final byte valuetype, final Object value) {
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
            graph.setRoot(root);
            root.setAt(_TOTAL, Type.INT, 0);
            root.setAt(_SUBNODES, Type.INT, 0);
            root.setAt(_MIN, Type.DOUBLE_ARRAY, min);
            root.setAt(_MAX, Type.DOUBLE_ARRAY, max);
        }
        internalInsert(root, keys, valuetype, value, IndexStrategy.REPLACE, min, max, getCenterMinMax(min, max), resolution, buffersize, root);

    }

    @Override
    public void profile(double[] keys, int occurance) {
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
            graph.setRoot(root);
            root.setAt(_TOTAL, Type.INT, 0);
            root.setAt(_SUBNODES, Type.INT, 0);
            root.setAt(_MIN, Type.DOUBLE_ARRAY, min);
            root.setAt(_MAX, Type.DOUBLE_ARRAY, max);
        }
        internalInsert(root, keys, Type.INT, occurance, IndexStrategy.PROFILE, min, max, getCenterMinMax(min, max), resolution, buffersize, root);

    }

    @Override
    public Object[] nearestN(double[] keys, int nbElem) {
        return new Object[0];
    }

    @Override
    public Object[] nearestWithinRadius(double[] keys, double radius) {
        return new Object[0];
    }

    @Override
    public Object[] nearestNWithinRadius(double[] keys, int nbElem, double radius) {
        return new Object[0];
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
