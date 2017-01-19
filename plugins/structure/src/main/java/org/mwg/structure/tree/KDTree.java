package org.mwg.structure.tree;

import org.mwg.Graph;
import org.mwg.Type;
import org.mwg.base.BaseNode;
import org.mwg.struct.ENode;
import org.mwg.structure.Tree;
import org.mwg.structure.TreeResult;
import org.mwg.structure.distance.Distance;
import org.mwg.structure.distance.Distances;

/**
 * Created by assaad on 18/01/2017.
 */
public class KDTree extends BaseNode implements Tree {
    public static String NAME = "KDTree";

    public static String RESOLUTION = "resolution";
    public static String BUFFER_SIZE = "buffer_size";
    public static String DISTANCE = "distance";
    public static String DISTANCE_THRESHOLD = "distance_threshold";
    private static String EGRAPH = "egraph";
    private static String STRATEGY = "strategy";

    public static int DISTANCE_DEF = Distances.DEFAULT;


    private static int E_SUBTREE_NODES = 0;
    private static int E_KEY = 1;
    private static int E_SUM_KEY = 2;
    private static int E_VALUE = 3;
    private static int E_SUBTREE_VALUES = 4;
    private static int E_RIGHT = 5;
    private static int E_LEFT = 6;

    public KDTree(long p_world, long p_time, long p_id, Graph p_graph) {
        super(p_world, p_time, p_id, p_graph);
    }


    private static boolean checkCreateLevels(double[] key1, double[] key2, double[] resolutions) {
        if (resolutions != null) {
            for (int i = 0; i < key2.length; i++) {
                if (Math.abs(key1[i] - key2[i]) > resolutions[i]) {
                    return true;
                }
            }
        } else {
            for (int i = 0; i < key2.length; i++) {
                if ((key1[i] != key2[i])) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean subInsert(final ENode node, final double[] key, final long value, final int strategyType, final int lev, final double[] resolution, final ENode root, final Distance distance) {

        double[] pKey = (double[]) node.getAt(E_KEY);
        if (pKey == null) {
            pKey = new double[key.length];
            System.arraycopy(key, 0, pKey, 0, key.length);
            node.setAt(E_KEY, Type.DOUBLE_ARRAY, pKey);
            node.setAt(E_VALUE, Type.LONG, value);
            if (strategyType == IndexStrategy.PROFILE) {
                node.setAt(E_SUBTREE_VALUES, Type.LONG, value);
                double[] sk = new double[pKey.length];
                for (int i = 0; i < key.length; i++) {
                    sk[i] = pKey[i] * value;
                }
                node.setAt(E_SUM_KEY, Type.DOUBLE_ARRAY, sk);
            }
            node.setAt(E_SUBTREE_NODES, Type.LONG, 1);
            return true;
        } else if (!checkCreateLevels(key, pKey, resolution)) {
            //Need to replace the key here if indexing
            if (strategyType == IndexStrategy.INDEX) {
                node.setAt(E_VALUE, Type.LONG, value);
            } else if (strategyType == IndexStrategy.PROFILE) {
                //need to update the keys and values of the profiles
                double[] sk = (double[]) node.getAt(E_SUM_KEY);
                for (int i = 0; i < pKey.length; i++) {
                    sk[i] = (sk[i] + key[i] * value);
                }
                node.setAt(E_SUM_KEY, Type.DOUBLE_ARRAY, sk);
                node.setAt(E_VALUE, Type.LONG, (long) node.getOrCreateAt(E_VALUE, Type.LONG) + value);
                node.setAt(E_SUBTREE_VALUES, Type.LONG, (long) node.getOrCreateAt(E_SUBTREE_VALUES, Type.LONG) + value);
            }
            return false;
        } else {
            ENode child;
            if (key[lev] > pKey[lev]) {
                child = (ENode) node.getAt(E_RIGHT);
                if (child == null) {
                    child = createNode(node, true);
                }
            } else {
                child = (ENode) node.getAt(E_LEFT);
                if (child == null) {
                    child = createNode(node, false);
                }
            }

            if (subInsert(child, key, value, strategyType, (lev + 1) % key.length, resolution, root, distance)) {
                //update parents reccursively
                if (strategyType == IndexStrategy.PROFILE) {
                    node.setAt(E_SUBTREE_VALUES, Type.LONG, (long) node.getAt(E_SUBTREE_VALUES) + value);
                }

                node.setAt(E_SUBTREE_NODES, Type.LONG, (long) node.getAt(E_SUBTREE_NODES) + 1);
                return true;
            } else {
                if (strategyType == IndexStrategy.PROFILE) {
                    node.setAt(E_SUBTREE_VALUES, Type.LONG, (long) node.getAt(E_SUBTREE_VALUES) + value);
                }
                return false;
            }
        }

    }

    private static ENode createNode(ENode parent, boolean right) {
        ENode child = parent.graph().newNode();
        if (right) {
            parent.setAt(E_RIGHT, Type.ENODE, child);
        } else {
            parent.setAt(E_LEFT, Type.ENODE, child);
        }

        return child;
    }


    @Override
    public void setDistance(int distanceType) {
        super.set(DISTANCE, Type.INT, distanceType);
    }

    @Override
    public void setDistanceThreshold(double distanceThreshold) {
        super.set(DISTANCE_THRESHOLD, Type.DOUBLE, distanceThreshold);
    }

    @Override
    public void insert(double[] keys, long value) {

    }

    @Override
    public void profile(double[] keys, long occurrence) {

    }

    @Override
    public TreeResult nearestN(double[] keys, int nbElem) {
        return null;
    }

    @Override
    public TreeResult nearestWithinRadius(double[] keys, double radius) {
        return null;
    }

    @Override
    public TreeResult nearestNWithinRadius(double[] keys, int nbElem, double radius) {
        return null;
    }

    @Override
    public TreeResult query(double[] min, double[] max) {
        return null;
    }

    @Override
    public long size() {
        return 0;
    }

    @Override
    public long numberOfNodes() {
        return 0;
    }
}
